package com.teamwizardry.refraction.common.network;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.refraction.api.Constants;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class PacketBeamParticle1 extends PacketBase {

    private Vec3d pos;
    private Color color;

    public PacketBeamParticle1() {
    }

    public PacketBeamParticle1(Vec3d pos, Color color) {
        this.pos = pos;
        this.color = color;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        color = new Color(buf.readInt(), true);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(pos.xCoord);
        buf.writeDouble(pos.yCoord);
        buf.writeDouble(pos.zCoord);
        buf.writeInt(color.getRGB());
    }

    @Override
    public void handle(MessageContext messageContext) {
        if (messageContext.side.isServer()) return;

        World world = Minecraft.getMinecraft().player.world;

        ParticleBuilder particle1 = new ParticleBuilder(3);
        particle1.setRender(new ResourceLocation(Constants.MOD_ID, "particles/star"));
        particle1.disableRandom();
        particle1.disableMotionCalculation();
        particle1.setAlphaFunction(new InterpFadeInOut(0f, 1f));
        particle1.setScale(ThreadLocalRandom.current().nextFloat() * 2);
        particle1.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
        ParticleSpawner.spawn(particle1, world, new StaticInterp<>(pos), 1);
    }
}
