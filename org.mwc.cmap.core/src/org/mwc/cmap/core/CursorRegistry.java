package org.mwc.cmap.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

public class CursorRegistry
{

	public static final String HAND_FIST = "hand_fist";
	public static final String HAND = "hand";
	public static final String SELECT_POINT_HIT_DOWN = "SelectPointHitDown";
	public static final String SELECT_POINT_HIT = "SelectPointHit";
	public static final String SELECT_POINT = "SelectPoint";
	
	public static final String SELECT_FEATURE_HIT_DOWN = "SelectFeatureHitDown";
	public static final String SELECT_FEATURE_HIT = "SelectFeatureHit";
	public static final String SELECT_FEATURE = "SelectFeature";
	public static final String SELECT_FEATURE_HIT_ROTATE = "SelectFeatureHitRotate";
	public static final String SELECT_FEATURE_HIT_STRETCH = "SelectFeatureHitStretch";
	public static final String SELECT_FEATURE_HIT_SHEAR = "SelectFeatureHitShear";
	public static final String SELECT_FEATURE_HIT_FAN_STRETCH = "SelectFeatureHitFanStretch";
	public static final String SELECT_FEATURE_HIT_DRAG = "SelectFeatureHitDrag";
	
	private static final Map<String, Cursor> cursors = new HashMap<String, Cursor>();

	public static Cursor getCursor(String id)
	{
		Cursor cursor = cursors.get(id);
		if (cursor == null || cursor.isDisposed())
		{
			if (HAND_FIST.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/hand_fist.ico").getImageData(), 4, 2);
				cursors.put(HAND_FIST, cursor);
				return cursor;
			}
			if (HAND.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/hand.ico").getImageData(), 4, 2);
				cursors.put(HAND, cursor);
				return cursor;
			}
			if (SELECT_POINT_HIT_DOWN.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/SelectPointHitDown.ico").getImageData(),
						7, 3);
				cursors.put(SELECT_POINT_HIT_DOWN, cursor);
				return cursor;
			}
			if (SELECT_POINT.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/SelectPoint.ico").getImageData(), 7, 3);
				cursors.put(SELECT_POINT, cursor);
				return cursor;
			}

			if (SELECT_POINT_HIT.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/SelectPointHit.ico").getImageData(), 7,
						3);
				cursors.put(SELECT_POINT_HIT, cursor);
				return cursor;
			}
			
			if (SELECT_FEATURE_HIT_DOWN.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/SelectFeatureHitDown.ico").getImageData(), 4,
						2);
				cursors.put(SELECT_FEATURE_HIT_DOWN, cursor);
				return cursor;
			}

			if (SELECT_FEATURE_HIT.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/SelectFeatureHit.ico").getImageData(), 4,
						2);
				cursors.put(SELECT_FEATURE_HIT, cursor);
				return cursor;
			}
			
			if (SELECT_FEATURE.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/SelectFeature.ico").getImageData(), 4,
						2);
				cursors.put(SELECT_FEATURE, cursor);
				return cursor;
			}
			
			if (SELECT_FEATURE_HIT_ROTATE.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/SelectFeatureHitRotate.ico")
						.getImageData(), 4, 2);
				cursors.put(SELECT_FEATURE_HIT_ROTATE, cursor);
				return cursor;
			}
		
			if (SELECT_FEATURE_HIT_STRETCH.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/SelectFeatureHitStretch.ico")
						.getImageData(), 4, 2);
				cursors.put(SELECT_FEATURE_HIT_STRETCH, cursor);
				return cursor;
			}
			
			if (SELECT_FEATURE_HIT_SHEAR.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/SelectFeatureHitShear.ico")
						.getImageData(), 4, 2);
				cursors.put(SELECT_FEATURE_HIT_SHEAR, cursor);
				return cursor;
			}
			
			if (SELECT_FEATURE_HIT_FAN_STRETCH.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/SelectFeatureHitFanStretch.ico")
						.getImageData(), 4, 2);
				cursors.put(SELECT_FEATURE_HIT_FAN_STRETCH, cursor);
				return cursor;
			}
			
			if (SELECT_FEATURE_HIT_DRAG.equals(id))
			{
				cursor = new Cursor(Display.getDefault(), CorePlugin
						.getImageDescriptor("icons/SelectFeatureHitDrag.ico")
						.getImageData(), 4, 2);
				cursors.put(SELECT_FEATURE_HIT_DRAG, cursor);
				return cursor;
			}
			
			
		}
		return cursor;
	}

}
