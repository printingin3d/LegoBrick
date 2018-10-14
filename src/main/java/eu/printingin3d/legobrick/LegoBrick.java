package eu.printingin3d.legobrick;

import java.util.ArrayList;
import java.util.List;

import eu.printingin3d.javascad.basic.Radius;
import eu.printingin3d.javascad.coords.Coords3d;
import eu.printingin3d.javascad.coords.Dims3d;
import eu.printingin3d.javascad.enums.Side;
import eu.printingin3d.javascad.models.Abstract3dModel;
import eu.printingin3d.javascad.models.Cube;
import eu.printingin3d.javascad.models.Cylinder;
import eu.printingin3d.javascad.tranzitions.Difference;
import eu.printingin3d.javascad.tranzitions.Union;

public class LegoBrick extends Union {
	private static final double ONE_SEGMENT_WIDTH = 8.0;
	private static final double HEIGHT = 9.6;
	private static final double HORIZONTAL_GAP = 2*0.1;
	private static final double WALL_THICKNESS = 1.2;
	private static final Radius AXLE_INNER_RADIUS = Radius.fromDiameter(4.8);
	private static final Radius AXLE_OUTER_RADIUS = Radius.fromDiameter(6.51);
	private static final Radius AXLE_ONE_RADIUS = Radius.fromDiameter(3.0);
	private static final Radius KNOB_RADIUS = Radius.fromDiameter(4.78);
	private static final double KNOB_HEIGTH = 1.8;
	private static final double THING_THICKNESS = 0.3;
	private static final double THING_WIDTH = 0.6;
	private static final double STABILIZER_THICKNESS = 0.8;
	private static final double STABILIZER_HEIGHT = 7.0;
	
	public LegoBrick(int xSize, int ySize) {
		super(getModels(xSize, ySize));
	}

	private static List<Abstract3dModel> getModels(int xSize, int ySize) {
		List<Abstract3dModel> models = new ArrayList<>();
		Abstract3dModel base = getBase(xSize, ySize);
		models.add(base);
		models.add(addAxles(base, xSize, ySize));
		models.add(getKnobs(base, xSize, ySize));
		return models;
	}

	private static Abstract3dModel getBase(int xSize, int ySize) {
		Abstract3dModel base = 
				new Cube(new Dims3d(ONE_SEGMENT_WIDTH*xSize-HORIZONTAL_GAP, ONE_SEGMENT_WIDTH*ySize-HORIZONTAL_GAP, HEIGHT))
					.subtractModel(
						new Cube(new Dims3d(ONE_SEGMENT_WIDTH*xSize-HORIZONTAL_GAP-WALL_THICKNESS*2, ONE_SEGMENT_WIDTH*ySize-HORIZONTAL_GAP-WALL_THICKNESS*2, HEIGHT-WALL_THICKNESS)).move(Coords3d.zOnly(-WALL_THICKNESS))						
				);
		List<Coords3d> xMoves = new ArrayList<>();
		for (int x=0;x<xSize;x++) {
			xMoves.add(Coords3d.xOnly((x-((xSize-1)*0.5))*ONE_SEGMENT_WIDTH));
		}
		List<Coords3d> yMoves = new ArrayList<>();
		for (int y=0;y<ySize;y++) {
			yMoves.add(Coords3d.yOnly((y-((ySize-1)*0.5))*ONE_SEGMENT_WIDTH));
		}
		
		return base.addModel(
			new Cube(new Dims3d(THING_WIDTH, THING_THICKNESS, HEIGHT))
				.moves(xMoves)
				.moves(Coords3d.yOnly(ySize*ONE_SEGMENT_WIDTH*0.5-WALL_THICKNESS-THING_THICKNESS+HORIZONTAL_GAP*0.5).createVariances())
			).addModel(
					new Cube(new Dims3d(THING_THICKNESS, THING_WIDTH, HEIGHT))
					.moves(yMoves)
					.moves(Coords3d.xOnly(xSize*ONE_SEGMENT_WIDTH*0.5-WALL_THICKNESS-THING_THICKNESS+HORIZONTAL_GAP*0.5).createVariances())
			);
	}

	private static Abstract3dModel getKnobs(Abstract3dModel base, int xSize, int ySize) {
		List<Coords3d> moves = new ArrayList<>();
		for (int x=0;x<xSize;x++) {
			for (int y=0;y<ySize;y++) {
				moves.add(new Coords3d((x-(xSize-1.0)/2.0)*ONE_SEGMENT_WIDTH, (y-(ySize-1.0)/2.0)*ONE_SEGMENT_WIDTH, 0.0));
			}
		}
		return getKnob(base).moves(moves);
	}
	
	private static Abstract3dModel addAxles(Abstract3dModel base, int xSize, int ySize) {
		List<Coords3d> moves = new ArrayList<>();
		if (xSize==1) {
			for (int y=0;y<ySize-1;y++) {
				moves.add(Coords3d.yOnly((y-(ySize-2.0)/2.0)*ONE_SEGMENT_WIDTH));
			}
			return getAxleOne().moves(moves);
		}
		else if (ySize==1) {
			for (int x=0;x<xSize-1;x++) {
				moves.add(Coords3d.xOnly((x-(xSize-2.0)/2.0)*ONE_SEGMENT_WIDTH));
			}
			return getAxleOne().moves(moves);
		}
		else {		
			for (int x=0;x<xSize-1;x++) {
				for (int y=0;y<ySize-1;y++) {
					moves.add(new Coords3d((x-(xSize-2.0)/2.0)*ONE_SEGMENT_WIDTH, (y-(ySize-2.0)/2.0)*ONE_SEGMENT_WIDTH, 0.0));
				}
			}
			Abstract3dModel result = getAxle().moves(moves);
			
			result = addXStabilizers(result, base, xSize);
			result = addYStabilizers(result, base, ySize);
			return result;
		}
	}
	
	private static Abstract3dModel addXStabilizers(Abstract3dModel result, Abstract3dModel base, int xSize) {
		List<Coords3d> moves = new ArrayList<>();
		for (int x=0;x<xSize-1;x++) {
			if ((x % 2) != (xSize % 2)) {
				moves.add(Coords3d.xOnly((x-(xSize-2.0)/2.0)*ONE_SEGMENT_WIDTH));
			}
		}
		if (!moves.isEmpty()) {
			Abstract3dModel xStabilizer = new Cube(new Dims3d(STABILIZER_THICKNESS, ONE_SEGMENT_WIDTH-AXLE_OUTER_RADIUS.getRadius(), STABILIZER_HEIGHT))
				.align(Side.TOP_IN, base)
				.moves(moves);
			result = result
					.addModel(xStabilizer.align(Side.BACK_IN, base))
					.addModel(xStabilizer.align(Side.FRONT_IN, base));
		}
		return result;
	}
	
	private static Abstract3dModel addYStabilizers(Abstract3dModel result, Abstract3dModel base, int ySize) {
		List<Coords3d> moves = new ArrayList<>();
		for (int y=0;y<ySize-1;y++) {
			if ((y % 2) != (ySize % 2)) {
				moves.add(Coords3d.yOnly((y-(ySize-2.0)/2.0)*ONE_SEGMENT_WIDTH));
			}
		}
		if (!moves.isEmpty()) {
			Abstract3dModel yStabilizer = new Cube(new Dims3d(ONE_SEGMENT_WIDTH-AXLE_OUTER_RADIUS.getRadius(), STABILIZER_THICKNESS, STABILIZER_HEIGHT))
					.align(Side.TOP_IN, base)
					.moves(moves);
			result = result
					.addModel(yStabilizer.align(Side.LEFT_IN, base))
					.addModel(yStabilizer.align(Side.RIGHT_IN, base));
		}
		return result;
	}
	
	private static Abstract3dModel getKnob(Abstract3dModel base) {
		return new Cylinder(KNOB_HEIGTH, KNOB_RADIUS).align(Side.TOP_OUT, base);
	}
	
	private static Abstract3dModel getAxle() {
		return new Difference(
				new Cylinder(HEIGHT, AXLE_OUTER_RADIUS),
				new Cylinder(HEIGHT, AXLE_INNER_RADIUS).move(Coords3d.MINUS_TINY)
		);
	}
	
	private static Abstract3dModel getAxleOne() {
		return new Cylinder(HEIGHT, AXLE_ONE_RADIUS);
	}
}
