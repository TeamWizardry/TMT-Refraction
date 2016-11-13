package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAXYZMarks extends PacketBase {

	private BlockPos[] positions, originPositions;
	private int dimension;

	public PacketAXYZMarks() {}

	public PacketAXYZMarks(BlockPos[] positions, BlockPos[] origins, int dimension) {
		this.positions = positions;
		this.originPositions = origins;
		this.dimension = dimension;
	}
	
	@Override
	public void handle(MessageContext ctx) {
		if (ctx.side.isServer()) return;

		World world = Minecraft.getMinecraft().theWorld;
		if (world.provider.getDimension() != dimension) return;

		for (int i = 0; i < positions.length; i++) {
			BlockPos pos = positions[i], origin = originPositions[i];
			// glitter goes here
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(positions.length);
		for (int i = 0; i < positions.length; i++) {
			buf.writeLong(positions[i].toLong());
			buf.writeLong(originPositions[i].toLong());
		}
		buf.writeInt(dimension);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int len = buf.readInt();
		positions = new BlockPos[len];
		originPositions = new BlockPos[len];
		for (int i = 0; i < len; i++) {
			positions[i] = BlockPos.fromLong(buf.readLong());
			originPositions[i] = BlockPos.fromLong(buf.readLong());
		}
		dimension = buf.readInt();
	}
}
