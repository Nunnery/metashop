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
package com.tealcube.minecraft.spigot.metashop.shops;

import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.spigot.metashop.MetaShopPlugin;
import com.tealcube.minecraft.spigot.metashop.utils.InventoryUtils;
import com.tealcube.minecraft.spigot.metashop.utils.MessageUtils;
import com.tealcube.minecraft.spigot.metashop.utils.TextUtils;
import ninja.amp.ampmenus.events.ItemClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopMenuItem extends MetaMenuItem {

    private final HiltItemStack itemToSell;

    private double price;

    public ShopMenuItem(HiltItemStack hiltItemStack, double price) {
        super(hiltItemStack.getName(), hiltItemStack, hiltItemStack.getLore());
        this.itemToSell = hiltItemStack;
        this.price = price;
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        super.onItemClick(event);
        double balance = MetaShopPlugin.getInstance().getEconomy().getBalance(event.getPlayer());
        if (balance < price) {
            event.setWillClose(false);
            event.setWillGoBack(false);
            event.setWillUpdate(false);
            return;
        }
        if (!InventoryUtils.canBeAddedToInventory(event.getPlayer().getInventory(), getItemToSell())) {
            event.setWillClose(false);
            event.setWillGoBack(false);
            event.setWillUpdate(true);
            MessageUtils.sendMessage(event.getPlayer(), MetaShopPlugin.getInstance().getSettings().getString("language.item-too-expensive"));
            return;
        }
        if (!MetaShopPlugin.getInstance().getEconomy().withdrawPlayer(event.getPlayer(), price).transactionSuccess()) {
            event.setWillClose(false);
            event.setWillGoBack(false);
            event.setWillUpdate(true);
            MessageUtils.sendMessage(event.getPlayer(), MetaShopPlugin.getInstance().getSettings().getString("language.item-too-expensive"));
            return;
        }
        event.getPlayer().getInventory().addItem(getItemToSell());
        event.setWillClose(false);
        event.setWillGoBack(false);
        event.setWillUpdate(true);
        MessageUtils.sendMessage(event.getPlayer(), MetaShopPlugin.getInstance().getSettings().getString("language.successful-purchase"));
    }

    @Override
    public ItemStack getFinalIcon(Player player) {
        HiltItemStack icon = new HiltItemStack(super.getFinalIcon(player));
        double balance = MetaShopPlugin.getInstance().getEconomy().getBalance(player);
        List<String> lore = icon.getLore();
        lore.add(TextUtils.color(MetaShopPlugin.getInstance().getSettings().getString("config.price-display")));
        if (balance < price) {
            lore.add(TextUtils.color(MetaShopPlugin.getInstance().getSettings().getString("language.item-too-expensive")));
        }
        lore = TextUtils.args(lore, new String[][] { { "%price%", getPrice() + "" } });
        icon.setLore(lore);
        return icon;
    }

    public HiltItemStack getItemToSell() {
        return itemToSell;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
