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


public class TimesCollectionConverter // extends CollectionConverter
{
//
//	private static final String XML_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
//	private static final String TIME_NODE = "time";
//	private static final String INFO_LIMPET_PLUGIN_ID = "info.limpet";
//	private static final SimpleDateFormat XML_DATE_FORMAT;
//
//	private static SimpleDateFormat getXmldateformat()
//  {
//    return XML_DATE_FORMAT;
//  }
//
//  static
//	{
//		XML_DATE_FORMAT = new SimpleDateFormat(XML_TIME_FORMAT);
//		XML_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
//	}
//
//	public TimesCollectionConverter(Mapper mapper)
//	{
//		super(mapper);
//	}
//
//	@SuppressWarnings("rawtypes")
//	@Override
//	public boolean canConvert(Class type)
//	{
//		return TimesList.class.isAssignableFrom(type);
//	}
//
//	@Override
//	public void marshal(Object source, HierarchicalStreamWriter writer,
//			MarshallingContext context)
//	{
//		@SuppressWarnings("unchecked")
//		TimesList<Long> times = (TimesList<Long>) source;
//		for (Long time : times)
//		{
//			String value = getXmldateformat().format(new Date(time));
//			writer.startNode(TIME_NODE);
//			context.convertAnother(value);
//			writer.endNode();
//		}
//	}
//
//	@Override
//	public Object unmarshal(HierarchicalStreamReader reader,
//			UnmarshallingContext context)
//	{
//		List<Long> times = new TimesList<>();
//		while (reader.hasMoreChildren())
//		{
//			reader.moveDown();
//			String item = (String) context.convertAnother(times, String.class);
//			Long value;
//			try
//			{
//				value = getXmldateformat().parse(item).getTime();
//			}
//			catch (ParseException e)
//			{
//				try
//				{
//					value = Long.valueOf(item);
//				}
//				catch (NumberFormatException e1)
//				{
//					log(e1);
//					value = new Date().getTime();
//				}
//			}
//			times.add(value);
//			reader.moveUp();
//		}
//		return times;
//	}
//
//	private void log(Throwable t)
//	{
//		Bundle bundle = Platform.getBundle(INFO_LIMPET_PLUGIN_ID);
//		if (bundle != null)
//		{
//			ILog log = Platform.getLog(bundle);
//			if (log != null)
//			{
//				log.log(new Status(IStatus.WARNING, bundle.getSymbolicName(),
//						"XStream time converter", t));
//				return;
//			}
//		}
//		t.printStackTrace();
//	}
//
}
