/*
 * All Rights Reserved (c) 2022 MoriyaShiine
 */

package moriyashiine.bewitchment.mixin.curse;

import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWCurses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EscapeDangerGoal.class)
public abstract class EscapeDangerGoalMixin extends Goal {
	@Shadow
	@Final
	protected PathAwareEntity mob;

	@Inject(method = "canStart", at = @At("RETURN"), cancellable = true)
	private void canStart(CallbackInfoReturnable<Boolean> callbackInfo) {
		if (!callbackInfo.getReturnValueZ() && !mob.getWorld().isClient && mob.age % 20 == 0 && !mob.getWorld().getEntitiesByClass(LivingEntity.class, new Box(mob.getBlockPos()).expand(8), living -> mob.canSee(living) && living.isAlive() && BWComponents.CURSES_COMPONENT.get(living).hasCurse(BWCurses.APATHY)).isEmpty()) {
			callbackInfo.setReturnValue(true);
		}
	}
}
