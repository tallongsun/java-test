package com.dl.xp;

import junit.framework.TestCase;

public class TestFrame extends TestCase{
	public TestFrame(String name) {
		super(name);
	}
	
	public void testScoreNoThrows() {
		Frame f = new Frame();
		assertEquals(0, f.getScore());
	}
	
	public void testAddOneThrow() {
		Game g = new Game();
		g.add(5);
		assertEquals(5, g.getScore());
	}
}
