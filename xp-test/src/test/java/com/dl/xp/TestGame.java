package com.dl.xp;

import junit.framework.TestCase;

public class TestGame extends TestCase{
	private Game g;
	
	public TestGame(String name) {
		super(name);
	}
	
	public void setUp() {
		g = new Game();
	}
	
	public void testTwoThrowsNoMark() {
		g.add(5);
		g.add(4);
		assertEquals(9, g.getScore());
	}
	
	public void testFourThrowsNoMark() {
		g.add(5);
		g.add(4);
		g.add(7);
		g.add(2);
		assertEquals(18, g.getScore());
		assertEquals(9, g.scoreForFrame(1));
		assertEquals(18, g.scoreForFrame(2));
	}
	
	public void testSimpleSpare() {
		g.add(3);
		g.add(7);
		g.add(3);
		assertEquals(13,g.scoreForFrame(1));
	}
	
	public void testSimpleFrameAfterSpare() {
		g.add(3);
		g.add(7);
		g.add(3);
		g.add(2);
		assertEquals(13,g.scoreForFrame(1));
		assertEquals(18,g.scoreForFrame(2));
		assertEquals(18,g.getScore());
	}
	
	public void test() {
		g.add(10);
		g.add(3);
		g.add(6);
		assertEquals(19,g.scoreForFrame(1));
		assertEquals(28,g.getScore());
		assertEquals(3,g.getCurrentFrame());
	}
	
	public void testPerfectGame() {
		for(int i=0;i<12;i++) {
			g.add(10);
		}
		assertEquals(300, g.getScore());
		assertEquals(11, g.getCurrentFrame());
	}
	
	public void testSampleGame() {
		g.add(1);
		g.add(4);
		g.add(4);
		g.add(5);
		g.add();
		g.add(1);
		g.add(1);
		g.add(1);
		g.add(1);
		g.add(1);
		g.add(1);
		g.add(1);
		g.add(1);
		g.add(1);
		g.add(1);
		g.add(1);
		g.add(1);
		g.add(1);
	}
}
