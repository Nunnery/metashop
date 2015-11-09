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

import com.tealcube.minecraft.spigot.metashop.MetaShopPlugin;
import com.tealcube.minecraft.spigot.metashop.common.Preconditions;

import java.util.HashMap;
import java.util.Map;

import ninja.amp.ampmenus.items.CloseItem;
import ninja.amp.ampmenus.menus.ItemMenu;

public class ShopMenu extends ItemMenu {

    private String id;
    private int closeItemIndex;
    private Map<Integer, ShopMenuItem> storeItems;

    public ShopMenu(String id, String name, int lines, int closeItemIndex) {
        super(name, Size.fit(lines * 9), MetaShopPlugin.getInstance());
        this.id = id;
        this.closeItemIndex = closeItemIndex;
        this.storeItems = new HashMap<>();
        if (closeItemIndex == -1) {
            return;
        }
        if (closeItemIndex < 0) {
            setItem(getSize().getSize() - 1, new CloseItem());
        } else {
            setItem(Math.max(getSize().getSize() - 1, closeItemIndex), new CloseItem());
        }
    }

    public String getId() {
        return id;
    }

    public int getCloseItemIndex() {
        return closeItemIndex;
    }

    public Map<Integer, ShopMenuItem> getStoreItems() {
        return storeItems;
    }

    public ShopMenu setItem(int index, ShopMenuItem item) {
        super.setItem(index, item);
        this.storeItems.put(index, item);
        return this;
    }

    public void update(Shop shop) {
        Preconditions.checkNotNull(shop);
        id = shop.getId();
        closeItemIndex = shop.getCloseItemIndex();
        storeItems = new HashMap<>();
        for (Map.Entry<Integer, Shop.Item> entry : shop.getItems().entrySet()) {
            setItem(entry.getKey(), new ShopMenuItem(entry.getValue().getHiltItemStack(), entry.getValue().getPrice()));
        }
    }

}
