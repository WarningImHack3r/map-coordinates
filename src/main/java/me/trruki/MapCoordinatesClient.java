package me.trruki;

import com.mojang.brigadier.Command;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.MapDecorations;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MapCoordinatesClient implements ClientModInitializer {

    private static final String FORMATTED_MOD_PREFIX = "[§dTreasure Map Coordinates§f] ";

    private static final String COMMAND_NAME = "mapcoords";

    private Pair<Double, Double> getMapCoordinates(ItemStack stack) {
        MapDecorations decorations = stack.get(DataComponents.MAP_DECORATIONS);
        if (decorations == null || decorations.decorations().isEmpty()) return null;

        MapDecorations.Entry mainDecoration = decorations.decorations().values().stream().findFirst().orElse(null);
        if (mainDecoration == null) return null;

        return new Pair<>(mainDecoration.x(), mainDecoration.z());
    }

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, _) ->
                dispatcher.register(ClientCommands.literal(COMMAND_NAME)
                        .executes(context -> {
                            Minecraft client = context.getSource().getClient();
                            if (client.player == null) return 0;

                            ItemStack mainItem = client.player.getInventory().getSelectedItem();
                            ItemStack offItem = client.player.getOffhandItem();
                            Map<String, ItemStack> items = Map.of(
                                    "Main hand", mainItem,
                                    "Off hand", offItem
                            );

                            boolean noMapFound = items.values().stream().noneMatch(item -> item.is(Items.FILLED_MAP));
                            if (noMapFound) {
                                context.getSource().sendError(Component.literal(FORMATTED_MOD_PREFIX + "§cYou are not holding a treasure map"));
                                return 0;
                            }

                            Map<String, Pair<Double, Double>> coordsMap = new HashMap<>();
                            items.forEach((name, item) -> coordsMap.put(name, getMapCoordinates(item)));
                            if (coordsMap.values().stream().allMatch(Objects::isNull)) {
                                context.getSource().sendError(Component.literal(FORMATTED_MOD_PREFIX + "§cYou are holding a map, but it's not a treasure map"));
                                return 0;
                            }

                            Map<String, Pair<Double, Double>> filteredMap = coordsMap.entrySet()
                                    .stream()
                                    .filter(entry -> entry.getValue() != null)
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                            String allCoordinates = filteredMap.entrySet()
                                    .stream()
                                    .map(entry -> filteredMap.size() > 1
                                            ? String.format("§f%s: §aX: %d §8| §aZ: %d§f", entry.getKey(), entry.getValue().getFirst().longValue(), entry.getValue().getSecond().longValue())
                                            : String.format("§aX: %d §8| §aZ: %d§f", entry.getValue().getFirst().longValue(), entry.getValue().getSecond().longValue())
                                    )
                                    .collect(Collectors.joining(" | "));
                            String allCoordinatesReadable = filteredMap.entrySet()
                                    .stream()
                                    .map(entry -> filteredMap.size() > 1
                                            ? String.format("%s: X=%d/Z=%d", entry.getKey(), entry.getValue().getFirst().longValue(), entry.getValue().getSecond().longValue())
                                            : String.format("X=%d/Z=%d", entry.getValue().getFirst().longValue(), entry.getValue().getSecond().longValue())
                                    )
                                    .collect(Collectors.joining(" | "));

                            context.getSource().sendFeedback(Component.literal(FORMATTED_MOD_PREFIX + allCoordinates)
                                    .setStyle(
                                            Style.EMPTY
                                                    .withClickEvent(new ClickEvent.CopyToClipboard(allCoordinatesReadable))
                                                    .withHoverEvent(
                                                            new HoverEvent.ShowText(
                                                                    Component.translatable("chat.copy.click")
                                                                            .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN))
                                                            )
                                                    )
                                    )
                            );

                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }
}
