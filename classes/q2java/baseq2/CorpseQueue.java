package q2java.baseq2;

import q2java.*;

/**
 * Keep a pile of corpses lying around the level
 * 
 */
class CorpseQueue 
	{
	protected final static int QUEUE_SIZE = 8;
	
	protected Corpse[] fQueue;
	protected int fPointer;
	
/**
 * Create a queue of corpses
 */
public CorpseQueue() 
	{
	fQueue = new Corpse[QUEUE_SIZE];
	
	for (int i = 0; i < QUEUE_SIZE; i++)	
		fQueue[i] = new Corpse();
				
	fPointer = 0;
	}
/**
 * Make a copy of an entity to keep around for a while.
 * @param ent NativeEntity
 */
void copyCorpse(NativeEntity ent) 
	{
	ent.unlinkEntity();
	fQueue[fPointer].copy(ent);	
	fPointer = (fPointer + 1) % QUEUE_SIZE;
	}
}