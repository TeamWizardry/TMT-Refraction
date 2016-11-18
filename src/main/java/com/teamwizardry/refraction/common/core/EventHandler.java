package com.teamwizardry.refraction.common.core;

import com.teamwizardry.refraction.api.EventAssemblyTableCraft;
import com.teamwizardry.refraction.common.network.PacketAXYZMarks;
import com.teamwizardry.refraction.init.ModAchievements;
import com.teamwizardry.refraction.init.ModBlocks;
import com.teamwizardry.refraction.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad44
 */
public class EventHandler {
	public static final EventHandler INSTANCE = new EventHandler();

	private EventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void worldUnload(WorldEvent.Unload event) {
		PacketAXYZMarks.controlPoints.clear();
	}

	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		PacketAXYZMarks.controlPoints.clear();
	}

	@SubscribeEvent
	public void craft(EventAssemblyTableCraft event) {
		if (event.getOutput().getItem() == ModBlocks.AXYZ.getItemForm())
			for (EntityPlayer player : getPlayersWithinRange(event.getWorld(), event.getPos(), 20))
				player.addStat(ModAchievements.TRANSLOCATOR);
		else if (event.getOutput().getItem() == ModBlocks.PRISM.getItemForm())
			for (EntityPlayer player : getPlayersWithinRange(event.getWorld(), event.getPos(), 20))
				player.addStat(ModAchievements.PRISM);
		else if (event.getOutput().getItem() == ModItems.GRENADE)
			for (EntityPlayer player : getPlayersWithinRange(event.getWorld(), event.getPos(), 20))
				player.addStat(ModAchievements.GRENADE);
	}

	public List<EntityPlayer> getPlayersWithinRange(World world, BlockPos pos, double range) {
		List<EntityPlayer> players = new ArrayList<>();
		for (int i = 0; i < world.playerEntities.size(); ++i) {
			EntityPlayer entityplayer = world.playerEntities.get(i);

			if (EntitySelectors.NOT_SPECTATING.apply(entityplayer)) {
				double d0 = entityplayer.getDistanceSq(pos);

				if (range < 0.0D || d0 < range * range) players.add(entityplayer);
			}
		}
		return players;
	}
}
