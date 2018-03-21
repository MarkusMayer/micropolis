// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine.tool;

import micropolisj.engine.Micropolis;
import micropolisj.engine.map.BuildingType;
import micropolisj.engine.map.MapPosition;

class BuildingTool extends ToolStroke
{
	public BuildingTool(Micropolis engine, MicropolisTool tool, int xpos, int ypos)
	{
		super(engine, tool, xpos, ypos);
	}
	
	@Override
	protected boolean hasPreview() {
		return false;
	}

	@Override
	protected boolean isDraggable() {
		return false;
	}

	@Override
	protected ToolPreview generatePreview() {
		throw new UnsupportedOperationException("No Preview available for Building Tool.");
	}

	@Override
	protected boolean useCityMapForBuild() {
		return true;
	}
	
	
	
}
