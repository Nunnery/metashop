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
import com.tealcube.minecraft.spigot.metashop.common.Preconditions;

import java.util.HashMap;
import java.util.Map;

public final class Shop {

    private final String id;
    private final int closeItemIndex;
    private final Map<Integer, Item> shopItems = new HashMap<>();

    public Shop(String id, int closeItemIndex) {
        Preconditions.checkNotNull(id);
        this.id = id;
        this.closeItemIndex = closeItemIndex;
    }

    public Map<Integer, Item> getItems() {
        return shopItems;
    }

    public Shop addItem(int index, Item item) {
        Preconditions.checkNotNull(item, "item cannot be null");
        Preconditions.checkArgument(index >= 0, "index cannot be less than 0");
        shopItems.put(index, item);
        return this;
    }

    public Item createItem(HiltItemStack hiltItemStack, double price) {
        return new Item(hiltItemStack, price);
    }

    public String getId() {
        return id;
    }

    public int getCloseItemIndex() {
        return closeItemIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Shop shop = (Shop) o;

        return getId().equals(shop.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public final class Item {
        private final HiltItemStack hiltItemStack;
        private final double price;

        public Item(HiltItemStack hiltItemStack, double price) {
            this.hiltItemStack = hiltItemStack;
            this.price = price;
        }

        public double getPrice() {
            return price;
        }

        public HiltItemStack getHiltItemStack() {
            return hiltItemStack;
        }
    }

}
