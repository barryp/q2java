package baseq2;

/**
 * Interface for classes that want to filter player damage.
 * @author Brian Haskin
 */
public interface DamageFilter
	{
	
/**
 * Method to implement in order to filter a player's damage.
 * @param DamageObject - damage to be filtered.
 */
public DamageObject filterDamage(DamageObject dmg);
}