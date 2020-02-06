
package org.mwc.debrief.dis.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class InputFileRuleScanner extends RuleBasedScanner {
	private static Color COMMENT_COLOR = new Color(Display.getCurrent(), new RGB(63, 127, 95));
	private static Color BLOCK_COLOR = new Color(Display.getCurrent(), new RGB(95, 2, 8));
	private static Color BLOCK_BACK_COLOR = new Color(Display.getCurrent(), new RGB(195, 192, 228));

	// the color

	public InputFileRuleScanner() {
		// get ready for list
		final IRule[] rules = new IRule[2];

		// start with comment marker
		final IToken commentToken = new Token(new TextAttribute(COMMENT_COLOR, null, SWT.ITALIC));
		rules[0] = (new EndOfLineRule("//", commentToken));

		// and a block token
		final IToken blockToken = new Token(new TextAttribute(BLOCK_COLOR, BLOCK_BACK_COLOR, SWT.NONE));
		rules[1] = (new EndOfLineRule("<<", blockToken));

		setRules(rules);
	}
}
