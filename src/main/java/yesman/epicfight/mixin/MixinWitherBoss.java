package yesman.epicfight.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.boss.WitherPatch;

@Mixin(value = WitherBoss.class)
public abstract class MixinWitherBoss extends Monster implements PowerableMob, RangedAttackMob {
	@Shadow @Final private final int[] nextHeadUpdate = new int[2];
	@Shadow @Final private final int[] idleHeadUpdates = new int[2];
	@Shadow @Final private ServerBossEvent bossEvent;
	@Shadow private int destroyBlocksTick;
	@Unique private WitherPatch epicfightPatch;
	
	protected MixinWitherBoss(EntityType<? extends WitherBoss> entityType, Level level) {
		super(entityType, level);
	}
	
	@Inject(at = @At(value = "RETURN"), method = "<init>")
	private void epixfight_witherBossInit(CallbackInfo info) {
		this.epicfightPatch = (WitherPatch)((WitherBoss)((Object)this)).getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
	}
	
	@Inject(at = @At(value = "HEAD"), method = "aiStep()V", cancellable = true)
	private void epicfight_aiStep(CallbackInfo info) {
		info.cancel();
		
		WitherBoss self = this.epicfightPatch.getOriginal();
		super.aiStep();
		
		for (int i = 0; i < 2; ++i) {
			self.yRotOHeads[i] = self.yRotHeads[i];
			self.xRotOHeads[i] = self.xRotHeads[i];
		}
		
		for (int j = 0; j < 2; ++j) {
			int k = self.getAlternativeTarget(j + 1);
			Entity entity1 = null;
			
			if (k > 0) {
				entity1 = this.level.getEntity(k);
			}
			
			if (this.epicfightPatch.getLaserTargetEntity(j + 1) != null) {
				Entity laserTarget = this.epicfightPatch.getLaserTargetEntity(j + 1);
				this.lookAt(j, laserTarget.getX(), laserTarget.getEyeY(), laserTarget.getZ(), 360.0F, 360.0F);
			} else if (isValid(this.epicfightPatch.getLaserTargetPosition(j + 1))) {
				Vec3 laserTargetPosition = this.epicfightPatch.getLaserTargetPosition(j + 1);
				this.lookAt(j, laserTargetPosition.x, laserTargetPosition.y, laserTargetPosition.z, 360.0F, 360.0F);
			} else if (this.epicfightPatch.getEntityState().inaction()) {
				self.xRotHeads[j] = this.rotlerp(self.xRotHeads[j], 0.0F, 40.0F);
				self.yRotHeads[j] = this.rotlerp(self.yRotHeads[j], this.yBodyRot, 10.0F);
			} else if (entity1 != null) {
				this.lookAt(j, entity1.getX(), entity1.getEyeY(), entity1.getZ(), 40.0F, 10.0F);
			} else {
				self.yRotHeads[j] = this.rotlerp(self.yRotHeads[j], this.yBodyRot, 10.0F);
			}
		}
		
		boolean powered = this.isPowered();
		
		for (int l = 0; l < 3; ++l) {
			double subHeadX = self.getHeadX(l);
			double subHeadY = self.getHeadY(l);
			double subHeadZ = self.getHeadZ(l);
			
			if (!this.epicfightPatch.isGhost()) {
				this.level.addParticle(ParticleTypes.SMOKE, subHeadX + this.random.nextGaussian() * (double) 0.3F, subHeadY + this.random.nextGaussian() * (double) 0.3F, subHeadZ + this.random.nextGaussian() * (double) 0.3F, 0.0D, 0.0D, 0.0D);
				
				if (powered && this.level.random.nextInt(4) == 0) {
					this.level.addParticle(ParticleTypes.ENTITY_EFFECT, subHeadX + this.random.nextGaussian() * (double) 0.3F, subHeadY + this.random.nextGaussian() * (double) 0.3F, subHeadZ + this.random.nextGaussian() * (double) 0.3F, (double) 0.7F, (double) 0.7F, 0.5D);
				}
			}
		}
		
		if (self.getInvulnerableTicks() > 0) {
			for (int i1 = 0; i1 < 3; ++i1) {
				this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + this.random.nextGaussian(), this.getY() + (double) (this.random.nextFloat() * 3.3F), this.getZ() + this.random.nextGaussian(), (double) 0.7F, (double) 0.7F, (double) 0.9F);
			}
		}
	}
	
	@Unique
	private void lookAt(int head, double x, double y, double z, float lerpX, float lerpY) {
		WitherBoss self = this.epicfightPatch.getOriginal();
		double d9 = self.getHeadX(head + 1);
		double d1 = self.getHeadY(head + 1);
		double d3 = self.getHeadZ(head + 1);
		double d4 = x - d9;
		double d5 = y - d1;
		double d6 = z - d3;
		double d7 = Math.sqrt(d4 * d4 + d6 * d6);
		float f = (float)(Mth.atan2(d6, d4) * (180F / Math.PI)) - 90.0F;
		float f1 = (float)(-(Mth.atan2(d5, d7) * (180F / Math.PI)));
		self.xRotHeads[head] = this.rotlerp(self.xRotHeads[head], f1, lerpX);
		self.yRotHeads[head] = this.rotlerp(self.yRotHeads[head], f, lerpY);
	}
	
	@Inject(at = @At(value = "HEAD"), method = "customServerAiStep()V", cancellable = true)
	private void epicfight_customServerAiStep(CallbackInfo info) {
		info.cancel();
		WitherBoss self = this.epicfightPatch.getOriginal();
		
		if (self.getInvulnerableTicks() > 0) {
			int k1 = self.getInvulnerableTicks() - 1;
			this.bossEvent.setProgress(1.0F - (float) k1 / 220.0F);
			
			if (k1 <= 0) {
				Explosion.BlockInteraction explosion$blockinteraction = ForgeEventFactory.getMobGriefingEvent(self.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
				self.level.explode(this, self.getX(), self.getEyeY(), self.getZ(), 7.0F, false, explosion$blockinteraction);
				
				if (!self.isSilent()) {
					self.level.globalLevelEvent(1023, self.blockPosition(), 0);
				}
			}
			
			self.setInvulnerableTicks(k1);
			if (self.tickCount % 10 == 0) {
				self.heal(10.0F);
			}
		} else {
			super.customServerAiStep();
			
			for (int i = 1; i < 3; ++i) {
				if (self.tickCount >= this.nextHeadUpdate[i - 1]) {
					this.nextHeadUpdate[i - 1] = self.tickCount + 10 + self.getRandom().nextInt(10);
					
					if ((self.level.getDifficulty() == Difficulty.NORMAL || self.level.getDifficulty() == Difficulty.HARD) && !this.epicfightPatch.getEntityState().inaction()) {
						int i3 = i - 1;
						int j3 = this.idleHeadUpdates[i - 1];
						this.idleHeadUpdates[i3] = this.idleHeadUpdates[i - 1] + 1;
						
						if (j3 > 15) {
							double d0 = Mth.nextDouble(self.getRandom(), self.getX() - 10.0D, self.getX() + 10.0D);
							double d1 = Mth.nextDouble(self.getRandom(), self.getY() - 5.0D, self.getY() + 5.0D);
							double d2 = Mth.nextDouble(self.getRandom(), self.getZ() - 10.0D, self.getZ() + 10.0D);
							self.performRangedAttack(i + 1, d0, d1, d2, true);
							this.idleHeadUpdates[i - 1] = 0;
						}
					}
					
					int l1 = self.getAlternativeTarget(i);
					
					if (this.epicfightPatch.getEntityState().inaction()) {
						this.nextHeadUpdate[i - 1] = self.tickCount + 30;
					}
					
					if (l1 > 0) {
						LivingEntity livingentity = (LivingEntity) self.level.getEntity(l1);
						
						if (livingentity != null && self.canAttack(livingentity) && !(self.distanceToSqr(livingentity) > 900.0D) && self.hasLineOfSight(livingentity)) {
							if (!this.epicfightPatch.getEntityState().inaction()) {
								self.performRangedAttack(i + 1, livingentity);
								this.nextHeadUpdate[i - 1] = self.tickCount + 40 + self.getRandom().nextInt(20);
								this.idleHeadUpdates[i - 1] = 0;
							}
						} else {
							self.setAlternativeTarget(i, 0);
						}
					} else {
						List<LivingEntity> list = self.level.getNearbyEntities(LivingEntity.class, WitherPatch.WTIHER_TARGETING_CONDITIONS, this, self.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));
						
						if (!list.isEmpty()) {
							LivingEntity livingentity1 = list.get(self.getRandom().nextInt(list.size()));
							self.setAlternativeTarget(i, livingentity1.getId());
						}
					}
				}
			}
			
			if (self.getTarget() != null) {
				self.setAlternativeTarget(0, self.getTarget().getId());
			} else {
				self.setAlternativeTarget(0, 0);
			}

			if (this.destroyBlocksTick > 0) {
				--this.destroyBlocksTick;
				
				if (this.destroyBlocksTick == 0 && ForgeEventFactory.getMobGriefingEvent(self.level, this)) {
					int j1 = Mth.floor(self.getY());
					int i2 = Mth.floor(self.getX());
					int j2 = Mth.floor(self.getZ());
					boolean flag = false;
					
					for (int j = -1; j <= 1; ++j) {
						for (int k2 = -1; k2 <= 1; ++k2) {
							for (int k = 0; k <= 3; ++k) {
								int l2 = i2 + j;
								int l = j1 + k;
								int i1 = j2 + k2;
								BlockPos blockpos = new BlockPos(l2, l, i1);
								BlockState blockstate = self.level.getBlockState(blockpos);
								
								if (blockstate.canEntityDestroy(self.level, blockpos, this) && ForgeEventFactory.onEntityDestroyBlock(this, blockpos, blockstate)) {
									flag = self.level.destroyBlock(blockpos, true, this) || flag;
								}
							}
						}
					}
					
					if (flag) {
						self.level.levelEvent((Player) null, 1022, self.blockPosition(), 0);
					}
				}
			}
			
			this.bossEvent.setProgress(self.getHealth() / self.getMaxHealth());
		}
	}
	
	@Unique
	public boolean isSpectator() {
		return this.epicfightPatch.isGhost();
	}
	
	@Unique
	protected SoundEvent getAmbientSound() {
		return this.epicfightPatch.isGhost() ? null : SoundEvents.WITHER_AMBIENT;
	}
	
	@Shadow
	public abstract float rotlerp(float p_31443_, float p_31444_, float p_31445_);
	
	@Override
	public void performRangedAttack(LivingEntity p_33317_, float p_33318_) {
		
	}
	
	@Override
	public boolean isPowered() {
		return this.getHealth() <= this.getMaxHealth() * 0.5F;
	}
	
	private static boolean isValid(Vec3 vec) {
		return !(Double.isNaN(vec.x)|| Double.isNaN(vec.y) || Double.isNaN(vec.z));
	}
}