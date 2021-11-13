package com.floweytf.channels.commands;

import com.floweytf.utils.Utils;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

public class Commands {
    public static LiteralArgumentBuilder<String> literal(String str) {
        return LiteralArgumentBuilder.literal(str);
    }
    public static <T> RequiredArgumentBuilder<String, T> argument(String pName, ArgumentType<T> pType) {
        return RequiredArgumentBuilder.argument(pName, pType);
    }
    public static <T, V> T getArgs(String name, CommandContext<V> ctx) {
        return ctx.getArgument(name, Utils.getClassOfGeneric());
    }
}
