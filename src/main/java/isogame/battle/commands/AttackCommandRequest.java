package isogame.battle.commands;

import java.util.Collection;
import java.util.stream.Collectors;

import isogame.battle.Ability;
import isogame.battle.BattleState;
import isogame.battle.Character;
import isogame.battle.DamageToTarget;
import isogame.engine.MapPoint;

public class AttackCommandRequest extends CommandRequest {
	private final MapPoint agent;
	private final MapPoint target;

	public AttackCommandRequest(MapPoint agent, MapPoint target) {
		this.agent = agent;
		this.target = target;
	}

	@Override
	public Command makeCommand(BattleState battleState) throws CommandException {
		Character a = battleState.getCharacterAt(agent);
		// TODO: fix this
		Ability ability = null; //a.getWeapon().info.attack;
		Collection<DamageToTarget> targets =
			battleState.getAbilityTargets(agent, ability, target).stream()
			.map(t -> ability.computeDamageToTarget(a, t))
			.collect(Collectors.toList());

		if (battleState.canAttack(agent, targets)) {
			return new AttackCommand(agent, targets);
		} else {
			throw new CommandException("Invalid ability command request");
		}
	}
}

