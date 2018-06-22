/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.data.persistence.xml;



public class LimpetCollectionConverter
{
//
//	public LimpetCollectionConverter(Mapper mapper)
//	{
//		super(mapper);
//	}
//
//	@SuppressWarnings("rawtypes")
//	@Override
//	public boolean canConvert(Class type)
//	{
//		return LimpetList.class.isAssignableFrom(type);
//	}
//
//	@Override
//	public void marshal(Object source, HierarchicalStreamWriter writer,
//			MarshallingContext context)
//	{
//		LimpetList<?> list = (LimpetList<?>) source;
//		if (list.size() == 0)
//		{
//			super.marshal(source, writer, context);
//			return;
//		}
//		Object object = list.get(0);
//		if (!(object instanceof Measure)) 
//		{
//			super.marshal(source, writer, context);
//			return;
//		}
//		@SuppressWarnings("unchecked")
//		LimpetList<Measure<?, Quantity>> measures = (LimpetList<Measure<?, Quantity>>) source;
//		boolean unitHandled = false;
//		for (Measure<?, Quantity> measure : measures)
//		{
//			if (!unitHandled)
//			{
//				unitHandled = true;
//				writeItem(measure.getUnit(), context, writer);
//			}
//			Object value = measure.getValue();
//			writeItem(value, context, writer);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public Object unmarshal(HierarchicalStreamReader reader,
//			UnmarshallingContext context)
//	{
//		List<Measure<?, Quantity>> measures = new LimpetList<>();
//		Unit<Quantity> unit = null;
//		Object item = null;
//		if (reader.hasMoreChildren())
//		{
//			reader.moveDown();
//			item = readItem(reader, context, measures);
//			if (item instanceof Unit)
//			{
//				unit = (Unit<Quantity>) item;
//			}
//			reader.moveUp();
//		}
//		if (unit == null)
//		{
//			@SuppressWarnings("rawtypes")
//			Collection collection = (Collection) createCollection(context.getRequiredType());
//			if (item != null)
//			{
//				collection.add(item);
//			}
//      populateCollection(reader, context, collection);
//      return collection;
//		}
//		while (reader.hasMoreChildren())
//		{
//			reader.moveDown();
//			item = readItem(reader, context, measures);
//			Double dbl = new Double(item.toString());
//			Measure<Double, Quantity> value = Measure.valueOf(dbl, unit);
//			measures.add(value);
//			reader.moveUp();
//		}
//		return measures;
//	}
//
}
