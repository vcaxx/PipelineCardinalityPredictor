package es.bsc.caos.schedulers;

import java.util.ArrayList;
import java.util.List;

public class Core {
	static int t2Capacity = 2;
	ArrayList<HWPipe> pipes;
	private int capacity;
	
	Core()
	{
		capacity = Core.t2Capacity;
		pipes = new ArrayList<HWPipe>();
	}
	
	Core(int cap)
	{
		capacity = cap;
		pipes = new ArrayList<HWPipe>();
	}
	
	Core(int cap, List<HWPipe> pips)
	{
		capacity = cap;
		pipes = new ArrayList<HWPipe>(pips);
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public ArrayList<HWPipe> getPipes()
	{
		return pipes;
	}
	
	public HWPipe getPipe(int pipNum)
	{
		if(pipNum < pipes.size())
			return pipes.get(pipNum);
		return null;
	}
	
	public boolean addPipe(HWPipe pip)
	{
		if(pipes.size() < capacity)
		{
			pipes.add(pip);
			return true;
		}
		return false;
	}
	
	/**
	 * Adds HWPipes to the core. If there is not enough
	 * space for all places the first N pipes are added.
	 * @param pips The list of HWPipe - s to add to the core
	 * @return number of pipelines added to the core 
	 */
	public int addPipes(List<HWPipe> pips)
	{
		int cnt = 0;
		while(pipes.size() < capacity)
		{
			pipes.add(pips.get(cnt++));
		}
		return cnt;
	}

}
