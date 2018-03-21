// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine.tool;

import java.util.Collections;
import java.util.List;

import micropolisj.engine.Sound;
import micropolisj.engine.map.MapPosition;
import micropolisj.engine.tool.ToolEvent.EventType;

class TranslatedToolEffect implements ToolEffectIfc
{
	final ToolEffectIfc base;
	final int dx;
	final int dy;

	TranslatedToolEffect(ToolEffectIfc base, int dx, int dy)
	{
		this.base = base;
		this.dx = dx;
		this.dy = dy;
	}

	//implements ToolEffectIfc
	public int getTile(int x, int y)
	{
		return base.getTile(x+dx, y+dy);
	}

	//implements ToolEffectIfc
	public void makeSound(int x, int y, Sound sound)
	{
		base.makeSound(x+dx, y+dy, sound);
	}

	//implements ToolEffectIfc
	public void setTile(int x, int y, int tileValue)
	{
		base.setTile(x+dx, y+dy, tileValue);
	}

	//implements ToolEffectIfc
	public void spend(int amount)
	{
		base.spend(amount);
	}

	//implements ToolEffectIfc
	public void toolResult(ToolResult tr)
	{
		base.toolResult(tr);
	}

	@Override
	public void addEvent(EventType ev,MapPosition relOffset) {
		base.addEvent(ev,relOffset.plus(MapPosition.at(dx, dy)));		
	}
	
	@Override
	public List<ToolEvent> getEvents() {
		return base.getEvents();
	}
}
