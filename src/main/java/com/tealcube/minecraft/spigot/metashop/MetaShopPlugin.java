/*
 * This file is part of MetaShop, licensed under the ISC License.
 *
 * Copyright (c) 2015 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package com.tealcube.minecraft.spigot.metashop;

import com.google.common.collect.Sets;
import com.tealcube.minecraft.bukkit.config.MasterConfiguration;
import com.tealcube.minecraft.bukkit.config.SmartYamlConfiguration;
import com.tealcube.minecraft.bukkit.config.VersionedSmartConfiguration;
import com.tealcube.minecraft.bukkit.config.VersionedSmartYamlConfiguration;
import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.spigot.metashop.commands.MetaShopCommand;
import com.tealcube.minecraft.spigot.metashop.managers.ShopManager;
import com.tealcube.minecraft.spigot.metashop.shops.Shop;
import com.tealcube.minecraft.spigot.metashop.shops.ShopItem;
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
                VersionedSmartConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYAML.update()) {
            getLogger().info("Updating config.yml");
        }
        languageYAML = new VersionedSmartYamlConfiguration(new File(getDataFolder(), "language.yml"), getResource("language.yml"),
                VersionedSmartConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
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
        File shopsDirectory = new File(getDataFolder(), "/shops/");
        if (!IOUtil.mkdirs(shopsDirectory)) {
            getLogger().info("Unable to make shops directory");
        }
        createExampleShopConfig(shopsDirectory);

        for (Shop shop : ShopManager.getShops()) {
            ShopManager.removeShop(shop);
        }

        // time for the whirlwind that is creating shops!
        for (String s : shopsDirectory.list()) {
            File f = new File(shopsDirectory, s);
            if (!f.exists() || s.equals("example.yml") || !s.endsWith(".yml")) {
                continue;
            }
            SmartYamlConfiguration shopConfig = new SmartYamlConfiguration(f);
            Shop shop = new Shop(s.replace(".yml", ""), shopConfig.getString("name"), shopConfig.getInt("number-of-lines", 2),
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
                    his.setName(TextUtils.color(itemSection.getString("name", "")));
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
                        index = RandomUtils.nextInt(shop.getSize().getSize());
                    }
                    ShopItem item = new ShopItem(his, itemSection.getDouble("price"));
                    shop.setItem(index, item);
                }
            }
            ShopManager.addShop(shop);
        }

        getLogger().info("Loaded shops: " + ShopManager.getShops().size());
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
        for (Shop shop : ShopManager.getShops()) {
            SmartYamlConfiguration shopConfig = new SmartYamlConfiguration(new File(getDataFolder(), "/shops/" + shop.getId() + ".yml"));
            for (String key : shopConfig.getKeys(true)) {
                shopConfig.set(key, null);
            }
            for (String key : shopConfig.getKeys(true)) {
                shopConfig.set(key, null);
            }
            shopConfig.set("name", shop.getName());
            shopConfig.set("number-of-lines", shop.getSize().ordinal() + 1);
            shopConfig.set("close-item-index", shop.getCloseItemIndex() <= -2 ? null : shop.getCloseItemIndex());
            int i = 0;
            for (Map.Entry<Integer, ShopItem> entry : shop.getStoreItems().entrySet()) {
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