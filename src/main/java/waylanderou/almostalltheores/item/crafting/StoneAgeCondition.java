package waylanderou.almostalltheores.item.crafting;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import waylanderou.almostalltheores.Config;

public class StoneAgeCondition implements ICondition {
	public static final ResourceLocation NAME = new ResourceLocation("almostalltheores", "stoneagecondition");
	public boolean stoneage;

	public StoneAgeCondition(Boolean value) { 
		this.stoneage = value;
	}

	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test() {		
		if(Config.enableStoneAge.get() && this.stoneage) {			
			return true;
		} else if(!Config.enableStoneAge.get() && !this.stoneage) {
			return true;
		} else return false;
	}

	@Override
	public String toString() {
		return "stoneagecondition";
	}

}
