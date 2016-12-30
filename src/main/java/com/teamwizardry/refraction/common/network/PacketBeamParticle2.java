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
public class PacketBeamParticle2 extends PacketBase {

    private Vec3d pos;
    private Color color;

    public PacketBeamParticle2() {
    }

    public PacketBeamParticle2(Vec3d pos, Color color) {
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

        ParticleBuilder particle2 = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 100));
        particle2.setRender(new ResourceLocation(Constants.MOD_ID, "particles/lens_flare_1"));
        particle2.disableRandom();
        particle2.disableMotionCalculation();
        particle2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), ThreadLocalRandom.current().nextInt(10, 15)));
        particle2.setAlphaFunction(new InterpFadeInOut((float) ThreadLocalRandom.current().nextDouble(0, 1), (float) ThreadLocalRandom.current().nextDouble(0, 1)));
        particle2.setScale((float) ThreadLocalRandom.current().nextDouble(0.5, 2.5));
        ParticleSpawner.spawn(particle2, world, new StaticInterp<>(pos), 1);
    }
}
