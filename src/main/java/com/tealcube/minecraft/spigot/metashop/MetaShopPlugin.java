/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tealcube.minecraft.spigot.metashop;

import com.google.common.collect.Sets;
import com.tealcube.minecraft.bukkit.config.*;
import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.spigot.metashop.commands.MetaShopCommand;
import com.tealcube.minecraft.spigot.metashop.managers.ShopMenuManager;
import com.tealcube.minecraft.spigot.metashop.shops.ShopMenu;
import com.tealcube.minecraft.spigot.metashop.shops.ShopMenuItem;
import com.tealcube.minecraft.spigot.metashop.utils.IOUtil;
import com.tealcube.minecraft.spigot.metashop.utils.TextUtils;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import se.ranzdo.bukkit.methodcommand.CommandHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class MetaShopPlugin extends JavaPlugin {

    private static MetaShopPlugin instance;
    private VersionedSmartYamlConfiguration configYAML;
    private VersionedSmartYamlConfiguration languageYAML;
    private MasterConfiguration settings;
    private Economy economy;
    private CommandHandler commandHandler;

    public static MetaShopPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        configYAML = new VersionedSmartYamlConfiguration(new File(getDataFolder(), "config.yml"), getResource("config.yml"),
                VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            getLogger().info("Updating config.yml");
        }
        languageYAML = new VersionedSmartYamlConfiguration(new File(getDataFolder(), "language.yml"), getResource("language.yml"),
                VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (languageYAML.update()) {
            getLogger().info("Updating language.yml");
        }

        settings = MasterConfiguration.loadFromFiles(configYAML, languageYAML);

        if (!setupEconomy()) {
            getLogger().info("Unable to find Vault or a Vault supported economy plugin");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        loadShops();

        this.commandHandler = new CommandHandler(this);
        this.commandHandler.registerCommands(new MetaShopCommand(this));
    }

    public void loadShops() {
        File shopsDirectory = new File(getDataFolder(), "shops/");
        if (!IOUtil.mkdirs(shopsDirectory)) {
            getLogger().info("Unable to make shops directory");
        }
        createExampleShopConfig(shopsDirectory);

        for (ShopMenu shopMenu : ShopMenuManager.getShops()) {
            ShopMenuManager.removeShop(shopMenu);
        }

        // time for the whirlwind that is creating shops!
        for (String s : shopsDirectory.list()) {
            File f = new File(shopsDirectory, s);
            if (!f.exists() || s.equals("example.yml") || !s.endsWith(".yml")) {
                continue;
            }
            SmartYamlConfiguration shopConfig = new SmartYamlConfiguration(f);
            ShopMenu shopMenu = new ShopMenu(s.replace(".yml", ""), shopConfig.getString("name"), shopConfig.getInt("number-of-lines", 2),
                                             shopConfig.getInt("close-item-index", -2));
            if (shopConfig.isConfigurationSection("items")) {
                ConfigurationSection section = shopConfig.getConfigurationSection("items");
                for (String key : section.getKeys(false)) {
                    if (!section.isConfigurationSection(key)) {
                        continue;
                    }
                    ConfigurationSection itemSection = section.getConfigurationSection(key);
                    if (!itemSection.isSet("material")) {
                        continue;
                    }
                    HiltItemStack his = new HiltItemStack(Material.valueOf(itemSection.getString("material")));
                    his.setAmount(itemSection.getInt("amount"));
                    if (itemSection.isSet("name")) {
                        his.setName(TextUtils.color(itemSection.getString("name", "")));
                    }
                    List<String> lore = itemSection.getStringList("lore");
                    his.setLore(TextUtils.color(lore));
                    if (itemSection.isConfigurationSection("enchantments")) {
                        for (String eKey : itemSection.getConfigurationSection("enchantments").getKeys(false)) {
                            his.addUnsafeEnchantment(Enchantment.getByName(eKey), itemSection.getInt("enchantments." + eKey));
                        }
                    }
                    if (itemSection.getBoolean("hide-flags")) {
                        his.setItemFlags(Sets.newHashSet(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE));
                    }
                    int index = itemSection.getInt("index");
                    if (index == -1) {
                        index = RandomUtils.nextInt(shopMenu.getSize().getSize());
                    }
                    ShopMenuItem item = new ShopMenuItem(his, itemSection.getDouble("price"));
                    shopMenu.setItem(index, item);
                }
            }
            ShopMenuManager.addShop(shopMenu);
        }

        getLogger().info("Loaded shops: " + ShopMenuManager.getShops().size());
    }

    private void createExampleShopConfig(File shopsDirectory) {
        File exampleShop = new File(shopsDirectory, "example.yml");
        if (!exampleShop.exists()) {
            SmartYamlConfiguration exampleShopConfig = new SmartYamlConfiguration(exampleShop);
            try {
                exampleShopConfig.load(new InputStreamReader(getResource("shops/example.yml")));
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            exampleShopConfig.save();
        }
    }

    @Override
    public void onDisable() {
        saveShops();
    }

    public void saveShops() {
        for (ShopMenu shopMenu : ShopMenuManager.getShops()) {
            SmartYamlConfiguration shopConfig = new SmartYamlConfiguration();
            shopConfig.set("name", shopMenu.getName());
            shopConfig.set("number-of-lines", shopMenu.getSize().ordinal() + 1);
            shopConfig.set("close-item-index", shopMenu.getCloseItemIndex() <= -2 ? null : shopMenu.getCloseItemIndex());
            int i = 0;
            for (Map.Entry<Integer, ShopMenuItem> entry : shopMenu.getStoreItems().entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null || entry.getValue().getItemToSell() == null) {
                    continue;
                }
                shopConfig.set("items." + (i) + ".index", entry.getKey());
                shopConfig.set("items." + (i) + ".material", entry.getValue().getItemToSell().getType().name());
                if (entry.getValue().getItemToSell().getName() != null) {
                    shopConfig.set("items." + (i) + ".name", TextUtils.decolor(entry.getValue().getItemToSell().getName()));
                } else {
                    shopConfig.set("items." + (i) + ".name", null);
                }
                if (entry.getValue().getItemToSell().getLore() != null) {
                    shopConfig.set("items." + (i) + ".lore", TextUtils.decolor(entry.getValue().getItemToSell().getLore()));
                }
                shopConfig.set("items." + (i) + ".amount", entry.getValue().getItemToSell().getAmount());
                shopConfig.set("items." + (i) + ".price", entry.getValue().getPrice());
                shopConfig.set("items." + (i) + ".hide-flags", entry.getValue().getItemToSell().getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES));
                for (Map.Entry<Enchantment, Integer> ent : entry.getValue().getItemToSell().getEnchantments().entrySet()) {
                    shopConfig.set("items." + (i) + ".enchantments." + ent.getKey().getName(), ent.getValue());
                }
                i += 1;
            }
            shopConfig.setFile(new File(getDataFolder(), "shops/" + shopMenu.getId() + ".yml"));
            shopConfig.save();
        }
    }

    public VersionedSmartYamlConfiguration getConfigYAML() {
        return configYAML;
    }

    public MasterConfiguration getSettings() {
        return settings;
    }

    public Economy getEconomy() {
        return economy;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public VersionedSmartYamlConfiguration getLanguageYAML() {
        return languageYAML;
    }
}