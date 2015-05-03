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

import com.tealcube.minecraft.spigot.metashop.MetaShopPlugin;
import ninja.amp.ampmenus.items.CloseItem;
import ninja.amp.ampmenus.menus.ItemMenu;

import java.util.HashMap;
import java.util.Map;

public class Shop extends ItemMenu {

    private final String id;
    private final int closeItemIndex;
    private final Map<Integer, ShopItem> storeItems;

    public Shop(String id, String name, int lines, int closeItemIndex) {
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

    public Map<Integer, ShopItem> getStoreItems() {
        return storeItems;
    }

    public void addItem(int index, ShopItem item) {
        this.storeItems.put(index, item);
        this.setItem(index, item);
    }

}
