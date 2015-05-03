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
package com.tealcube.minecraft.spigot.metashop.shops;

import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.spigot.metashop.MetaShopPlugin;
import com.tealcube.minecraft.spigot.metashop.utils.MessageUtils;
import com.tealcube.minecraft.spigot.metashop.utils.TextUtils;
import ninja.amp.ampmenus.events.ItemClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopItem extends MetaMenuItem {

    private final HiltItemStack itemToSell;

    private double price;

    public ShopItem(HiltItemStack hiltItemStack, double price) {
        super(hiltItemStack.getName(), hiltItemStack,
                TextUtils.args(hiltItemStack.getLore(), new String[][]{{"%price%", "" + price}}));
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
        if (!MetaShopPlugin.getInstance().getEconomy().withdrawPlayer(event.getPlayer(), price).transactionSuccess()) {
            event.setWillClose(false);
            event.setWillGoBack(false);
            event.setWillUpdate(true);
            MessageUtils.sendMessage(event.getPlayer(),
                    MetaShopPlugin.getInstance().getSettings().getString("language.item-too-expensive"));
            return;
        }
        event.getPlayer().getInventory().addItem(getItemToSell());
        event.setWillClose(false);
        event.setWillGoBack(false);
        event.setWillUpdate(true);
        MessageUtils.sendMessage(event.getPlayer(),
                MetaShopPlugin.getInstance().getSettings().getString("language.successful-purchase"));
    }

    @Override
    public ItemStack getFinalIcon(Player player) {
        HiltItemStack icon = new HiltItemStack(super.getFinalIcon(player));
        double balance = MetaShopPlugin.getInstance().getEconomy().getBalance(player);
        if (balance < price) {
            List<String> newLore = icon.getLore();
            newLore.add(TextUtils.color(MetaShopPlugin.getInstance().getSettings().getString("language.item-too-expensive")));
            icon.setLore(newLore);
        }
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
