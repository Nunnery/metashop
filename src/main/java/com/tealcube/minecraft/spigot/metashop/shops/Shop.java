package com.tealcube.minecraft.spigot.metashop.shops;

import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.spigot.metashop.common.Preconditions;

import java.util.HashMap;
import java.util.Map;

public final class Shop {

    private final String id;
    private final Map<Integer, Item> shopItems = new HashMap<>();

    public Shop(String id) {
        this.id = id;
    }

    public Map<Integer, Item> getShopItems() {
        return shopItems;
    }

    public Shop addItem(int index, Item item) {
        Preconditions.checkNotNull(item, "item cannot be null");
        Preconditions.checkArgument(index >= 0, "index cannot be less than 0");
        shopItems.put(index, item);
        return this;
    }

    public String getId() {
        return id;
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
