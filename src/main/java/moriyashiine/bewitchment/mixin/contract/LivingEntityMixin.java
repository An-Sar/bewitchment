package moriyashiine.bewitchment.mixin.contract;

import moriyashiine.bewitchment.api.component.ContractsComponent;
import moriyashiine.bewitchment.common.registry.BWContracts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("ConstantConditions")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	public abstract boolean addStatusEffect(StatusEffectInstance effect);
	
	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@ModifyVariable(method = "applyArmorToDamage", at = @At("HEAD"))
	private float modifyDamage(float amount, DamageSource source) {
		if (!world.isClient) {
			Entity directSource = source.getSource();
			if ((Object) this instanceof PlayerEntity player && ContractsComponent.get(player).hasContract(BWContracts.FAMINE)) {
				amount *= (1 - (0.035f * (20 - player.getHungerManager().getFoodLevel())));
			}
			if (directSource instanceof PlayerEntity player && ContractsComponent.get(player).hasContract(BWContracts.WRATH)) {
				amount *= (1 + (0.035f * player.getMaxHealth() - player.getHealth()));
			}
			if (directSource instanceof PlayerEntity player && ContractsComponent.get(player).hasContract(BWContracts.PESTILENCE)) {
				addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100));
				addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100));
				addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100));
			}
		}
		return amount;
	}
}
