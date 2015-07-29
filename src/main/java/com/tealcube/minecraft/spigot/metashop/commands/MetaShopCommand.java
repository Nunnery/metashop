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
package com.tealcube.minecraft.spigot.metashop.commands;

import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.spigot.metashop.MetaShopPlugin;
import com.tealcube.minecraft.spigot.metashop.managers.SessionManager;
import com.tealcube.minecraft.spigot.metashop.managers.ShopManager;
import com.tealcube.minecraft.spigot.metashop.managers.ShopMenuManager;
import com.tealcube.minecraft.spigot.metashop.sessions.ShopEditSession;
import com.tealcube.minecraft.spigot.metashop.shops.ShopMenu;
import com.tealcube.minecraft.spigot.metashop.shops.ShopMenuItem;
import com.tealcube.minecraft.spigot.metashop.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;

public class MetaShopCommand {

    private final MetaShopPlugin plugin;

    public MetaShopCommand(MetaShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(identifier = "metashop show", permissions = "metashop.command.show", onlyPlayers = false)
    public void showSubcommand(CommandSender sender, @Arg(name = "shop") String shopName, @Arg(name = "target", def = "?sender") Player target) {
        ShopMenu shopMenu = ShopMenuManager.getShopMenu(shopName);
        if (shopMenu == null) {
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.unable-to-open-sender"));
            if (!sender.equals(target)) {
                MessageUtils.sendMessage(target, plugin.getSettings().getString("language.unable-to-open-receiver"));
            }
            return;
        }
        shopMenu.update(ShopManager.getShop(shopName));
        shopMenu.open(target);
        MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.successful-open"));
    }

    @Command(identifier = "metashop list", permissions = "metashop.command.list", onlyPlayers = false)
    public void listSubcommand(CommandSender sender) {
        MessageUtils.sendMessage(sender, "<white>Use the first name given below.");
        for (ShopMenu shopMenu : ShopMenuManager.getShopMenus()) {
            MessageUtils.sendMessage(sender, shopMenu.getId() + " : " + shopMenu.getName());
        }
    }

    @Command(identifier = "metashop select", permissions = "metashop.command.select", onlyPlayers = true)
    public void selectSubcommand(Player sender, @Arg(name = "shop name") String shopName) {
        ShopMenu shopMenu = ShopMenuManager.getShopMenu(shopName);
        if (shopMenu == null) {
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.unable-to-select-shop"));
            return;
        }
        ShopEditSession shopEditSession = new ShopEditSession(sender.getUniqueId());
        shopEditSession.setShopId(shopMenu.getId());
        SessionManager.addShopEditSession(shopEditSession);
        MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.successful-shop-selection"));
    }

    @Command(identifier = "metashop add", permissions = "metashop.command.add", onlyPlayers = true)
    public void addSubcommand(Player sender, @Arg(name = "index") int index, @Arg(name = "price") double price) {
        if (!SessionManager.hasShopEditSession(sender.getUniqueId())) {
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.no-session"));
            return;
        }
        ShopEditSession session = SessionManager.getShopEditSession(sender.getUniqueId());
        if (session.getShopId() == null) {
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.no-session"));
            return;
        }
        if (sender.getItemInHand() == null || sender.getItemInHand().getType() == Material.AIR) {
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.unsupported-item"));
            return;
        }
        HiltItemStack his = new HiltItemStack(sender.getItemInHand());
        ShopMenu shopMenu = ShopMenuManager.getShopMenu(session.getShopId());
        if (shopMenu == null) {
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.shop-does-not-exist"));
            return;
        }
        ShopMenuItem item = new ShopMenuItem(his, price);
        shopMenu.setItem(index, item);
        MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.successful-add-item"));
    }

    @Command(identifier = "metashop remove", permissions = "metashop.command.remove", onlyPlayers = true)
    public void removeSubcommand(Player sender, @Arg(name = "index") int index) {
        if (!SessionManager.hasShopEditSession(sender.getUniqueId())) {
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.no-session"));
            return;
        }
        ShopEditSession session = SessionManager.getShopEditSession(sender.getUniqueId());
        if (session.getShopId() == null) {
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.no-session"));
            return;
        }
        ShopMenu shopMenu = ShopMenuManager.getShopMenu(session.getShopId());
        if (shopMenu == null) {
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.shop-does-not-exist"));
            return;
        }
        shopMenu.setItem(index, null);
        MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.successful-remove-item"));
    }

    @Command(identifier = "metashop save", permissions = "metashop.command.save", onlyPlayers = false)
    public void saveSubcommand(CommandSender sender) {
        plugin.saveShops();
        MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.save"));
    }

    @Command(identifier = "metashop load", permissions = "metashop.command.save", onlyPlayers = false)
    public void loadSubcommand(CommandSender sender) {
        plugin.loadShops();
        MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.load"));
    }

}
