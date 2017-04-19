package com.travel.booking_kuwait.model;

public class Room {

	public int adult_count = 2;
	public int child_count = 0;
	public int child1age	= 2;
	public int child2age	= 2;
	public int child3age	= 2;
	
	public int getAdultCount()
	{
		return this.adult_count;
	}

	public int getChildCount()
	{
		return this.child_count;
	}
	
	public int getChild1Age()
	{
		return this.child1age;
	}
	
	public int getChild2Age()
	{
		return this.child2age;
	}
	
	public int getChild3Age()
	{
		return this.child3age;
	}
	
	public void setAdultCount(int adult_count)
	{
		this.adult_count = adult_count;
	}
	
	public void setChildCount(int child_count)
	{
		this.child_count = child_count;
	}
	
	public void setChild1Age(int child1Age)
	{
		this.child1age = child1Age;
	}
	
	public void setChild2Age(int child2Age)
	{
		this.child2age = child2Age;
	}
	
	public void setChild3Age(int child3Age)
	{
		this.child3age = child3Age;
	}
	
}
