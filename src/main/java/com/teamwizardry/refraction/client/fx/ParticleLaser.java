package com.teamwizardry.refraction.client.fx;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Created by TheCodeWarrior
 */
public class ParticleLaser /* extends QueuedParticle<ParticleLaser> */{
	/*public static final ParticleRenderQueue<ParticleLaser> queue = new ParticleRenderQueue<ParticleLaser>(false) {
		@Override
		public String name() {
			return "laser";
		}
		
		@Override
		public void renderParticles(Tessellator tessellator) {
			RenderLaserUtil.startRenderingLasers();
			for(ParticleLaser laser : getRenderQueue()) {
				RenderLaserUtil.renderLaser(laser.color == null ? Color.WHITE : laser.color, laser.start, laser.end);
			}
			RenderLaserUtil.finishRenderingLasers();
		}
	};*/
	
	protected Vec3d start, end;
	protected Color color;
	
	public ParticleLaser(World worldIn, double posXIn, double posYIn, double posZIn, double endX, double endY, double endZ, Color color) {
		/*super(worldIn, posXIn, posYIn, posZIn);
		start = new Vec3d(posXIn, posYIn, posZIn);
		end = new Vec3d(endX, endY, endZ);
		this.color = color;
		
		setEntityBoundingBox(new AxisAlignedBB(posXIn, posYIn, posZIn, endX, endY, endZ));
		*/
	}
	
/*	@Override
	protected ParticleRenderQueue<ParticleLaser> queue() {
		return queue;
	}*/
}
