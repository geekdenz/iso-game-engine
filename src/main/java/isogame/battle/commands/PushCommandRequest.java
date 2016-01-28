package isogame.battle.commands;

import isogame.battle.BattleState;
import isogame.battle.Character;
import isogame.battle.Stats;
import isogame.battle.Targetable;
import isogame.engine.MapPoint;

public class PushCommandRequest extends CommandRequest {
	private final MapPoint agent;
	private final MapPoint target;

	public PushCommandRequest(MapPoint agent, MapPoint target) {
		this.agent = agent;
		this.target = target;
	}

	@Override
	public Command makeCommand(BattleState battleState) throws CommandException {
		Character a = battleState.getCharacterAt(agent);
		Targetable t = battleState.getTargetableAt(target);

		if (a != null && t != null) {
			boolean effective;
			if (!t.isPushable()) effective = false;
			else if (a.getPlayer() == t.getPlayer()) effective = true;
			else effective = Math.random() <= chanceOfSuccess(a.getStats(), t.getStats());

			if (battleState.canPush(agent, target, effective)) {
				return new PushCommand(agent, target, effective);
			}
		}

		throw new CommandException("Invalid push command request");
	}

	public double chanceOfSuccess(Stats a, Stats t) {
		return a.strength / t.strength * 0.7;
	}
}

