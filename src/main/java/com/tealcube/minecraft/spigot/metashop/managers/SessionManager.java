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
package com.tealcube.minecraft.spigot.metashop.managers;

import com.tealcube.minecraft.spigot.metashop.common.Preconditions;
import com.tealcube.minecraft.spigot.metashop.sessions.ShopEditSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SessionManager {

    private static final Map<UUID, ShopEditSession> SHOP_EDIT_SESSION_MAP = new HashMap<>();

    private SessionManager() {
        // do nothing
    }

    public static boolean addShopEditSession(ShopEditSession shopEditSession) {
        Preconditions.checkNotNull(shopEditSession);
        SHOP_EDIT_SESSION_MAP.put(shopEditSession.getOwner(), shopEditSession);
        return hasShopEditSession(shopEditSession);
    }

    public static boolean hasShopEditSession(ShopEditSession shopEditSession) {
        Preconditions.checkNotNull(shopEditSession);
        return SHOP_EDIT_SESSION_MAP.containsKey(shopEditSession.getOwner());
    }

    public static boolean hasShopEditSession(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return SHOP_EDIT_SESSION_MAP.containsKey(uuid);
    }

    public static boolean removeShopEditSession(ShopEditSession shopEditSession) {
        Preconditions.checkNotNull(shopEditSession);
        SHOP_EDIT_SESSION_MAP.remove(shopEditSession.getOwner());
        return !hasShopEditSession(shopEditSession);
    }

    public static boolean removeShopEditSession(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        SHOP_EDIT_SESSION_MAP.remove(uuid);
        return !hasShopEditSession(uuid);
    }

    public static ShopEditSession getShopEditSession(UUID uuid) {
        Preconditions.checkNotNull(uuid);
        return SHOP_EDIT_SESSION_MAP.get(uuid);
    }

}
