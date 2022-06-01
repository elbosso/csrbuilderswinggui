/*
 * Copyright (c) 2019.
 *
 * Juergen Key. Alle Rechte vorbehalten.
 *
 * Weiterverbreitung und Verwendung in nichtkompilierter oder kompilierter Form,
 * mit oder ohne Veraenderung, sind unter den folgenden Bedingungen zulaessig:
 *
 *    1. Weiterverbreitete nichtkompilierte Exemplare muessen das obige Copyright,
 * die Liste der Bedingungen und den folgenden Haftungsausschluss im Quelltext
 * enthalten.
 *    2. Weiterverbreitete kompilierte Exemplare muessen das obige Copyright,
 * die Liste der Bedingungen und den folgenden Haftungsausschluss in der
 * Dokumentation und/oder anderen Materialien, die mit dem Exemplar verbreitet
 * werden, enthalten.
 *    3. Weder der Name des Autors noch die Namen der Beitragsleistenden
 * duerfen zum Kennzeichnen oder Bewerben von Produkten, die von dieser Software
 * abgeleitet wurden, ohne spezielle vorherige schriftliche Genehmigung verwendet
 * werden.
 *
 * DIESE SOFTWARE WIRD VOM AUTOR UND DEN BEITRAGSLEISTENDEN OHNE
 * JEGLICHE SPEZIELLE ODER IMPLIZIERTE GARANTIEN ZUR VERFUEGUNG GESTELLT, DIE
 * UNTER ANDEREM EINSCHLIESSEN: DIE IMPLIZIERTE GARANTIE DER VERWENDBARKEIT DER
 * SOFTWARE FUER EINEN BESTIMMTEN ZWECK. AUF KEINEN FALL IST DER AUTOR
 * ODER DIE BEITRAGSLEISTENDEN FUER IRGENDWELCHE DIREKTEN, INDIREKTEN,
 * ZUFAELLIGEN, SPEZIELLEN, BEISPIELHAFTEN ODER FOLGENDEN SCHAEDEN (UNTER ANDEREM
 * VERSCHAFFEN VON ERSATZGUETERN ODER -DIENSTLEISTUNGEN; EINSCHRAENKUNG DER
 * NUTZUNGSFAEHIGKEIT; VERLUST VON NUTZUNGSFAEHIGKEIT; DATEN; PROFIT ODER
 * GESCHAEFTSUNTERBRECHUNG), WIE AUCH IMMER VERURSACHT UND UNTER WELCHER
 * VERPFLICHTUNG AUCH IMMER, OB IN VERTRAG, STRIKTER VERPFLICHTUNG ODER
 * UNERLAUBTE HANDLUNG (INKLUSIVE FAHRLAESSIGKEIT) VERANTWORTLICH, AUF WELCHEM
 * WEG SIE AUCH IMMER DURCH DIE BENUTZUNG DIESER SOFTWARE ENTSTANDEN SIND, SOGAR,
 * WENN SIE AUF DIE MOEGLICHKEIT EINES SOLCHEN SCHADENS HINGEWIESEN WORDEN SIND.
 *
 */

package de.elbosso.tools.csrbuilderswinggui;

import de.elbosso.ui.components.Inet4AddressPanel;
import de.elbosso.util.pattern.command.PrepareEmailAction;
import de.elbosso.util.pattern.command.PrepareEmailConfImpl;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.operator.OperatorCreationException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CSRBuilderGui extends javax.swing.JFrame implements javax.swing.event.DocumentListener
	,java.awt.event.FocusListener
{
	private OpenSSLConfParser openSSLConfParser;
	private CSRBuilder gcsr;
	private de.netsysit.util.pattern.command.ChooseFileAction action;
	private javax.swing.Action sendAction;
	private javax.swing.JPasswordField passwordf;
	private javax.swing.JPasswordField verificationf;
	private java.util.Map<java.lang.String, de.netsysit.util.validator.Rule> ruleMap;
	private de.netsysit.ui.components.FormPanel fp;

	public CSRBuilderGui() throws IOException
	{
		super("CSRBuilder");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		java.io.File encryption=new java.io.File("/home/elbosso/work/expect-dialog-ca.git/Dama11_Identity_CA/etc/encryption.conf");
		java.io.File smime=new java.io.File("/home/elbosso/work/expect-dialog-ca.git/Dama11_Identity_CA/etc/smime.conf");
		java.io.File server=new java.io.File("/home/elbosso/work/expect-dialog-ca.git/Dama11_Component_CA/etc/server.conf");

		javax.swing.JFileChooser fc=new javax.swing.JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		if(server.exists())
			fc.setSelectedFile(server);
		if((fc.showOpenDialog(null)== JFileChooser.APPROVE_OPTION)&&(fc.getSelectedFile()!=null))
		{

			ruleMap = new java.util.HashMap();

			openSSLConfParser = new OpenSSLConfParser(fc.getSelectedFile().toURI().toURL());
			gcsr = new CSRBuilder(openSSLConfParser.getKeyLengthInBits());

			fp = new de.netsysit.ui.components.FormPanel(new java.awt.Insets(5, 0, 5, 0));
			javax.swing.JPasswordField pw = new javax.swing.JPasswordField();
			pw.getDocument().addDocumentListener(this);
			pw.addFocusListener(this);
			de.netsysit.util.validator.rules.MinMaxLengthRule rule = new de.netsysit.util.validator.rules.MinMaxLengthRule(6, Integer.MAX_VALUE);
			//		de.netsysit.util.validator.Utilities.hookupTextComponentWithRule(pw,rule);
			ruleMap.put("password", rule);
			passwordf = pw;
			fp.addRow("password", "password", pw);
			pw = new javax.swing.JPasswordField();
			pw.getDocument().addDocumentListener(this);
			pw.addFocusListener(this);
			//		de.netsysit.util.validator.Utilities.hookupTextComponentWithRule(pw,rule);
			ruleMap.put("verification", rule);
			verificationf = pw;
			fp.addRow("verification", "verification", pw);
			for (DnSpec spec : openSSLConfParser.getDnSpecs())
			{
				javax.swing.JTextField tf = new javax.swing.JTextField();
				tf.addFocusListener(this);
				fp.addRow(spec.getName(), spec.getName(), tf);
				tf.setText(spec.getDef());
				tf.getDocument().addDocumentListener(this);
				de.netsysit.util.validator.rules.MinMaxLengthRule mmrule = new de.netsysit.util.validator.rules.MinMaxLengthRule(spec.getMinChars(), spec.getMaxChars());
				//			de.netsysit.util.validator.Utilities.hookupTextComponentWithRule(tf,mmrule);
				ruleMap.put(spec.getName(), mmrule);
			}

			createActions();
			javax.swing.JPanel toplevel = new javax.swing.JPanel(new java.awt.BorderLayout());
			toplevel.add(fp);
			javax.swing.JToolBar tb = new javax.swing.JToolBar();
			tb.setFloatable(false);
			tb.add(action);
			tb.add(sendAction);
			toplevel.add(tb, BorderLayout.NORTH);
			setContentPane(toplevel);
			fp.setPreferredSize(new java.awt.Dimension(fp.getPreferredSize().width + 228, fp.getPreferredSize().height));
			pack();
			updateState();
			setVisible(true);
		}
	}
	private void createActions()
	{
		de.netsysit.util.pattern.command.FileProcessor fp=new de.netsysit.util.pattern.command.FileProcessor()
		{
			@Override
			public boolean process(File[] files)
			{
				try
				{
					FileOutputStream fos = new FileOutputStream(files[0]);
					ZipOutputStream zipOut = new ZipOutputStream(fos);
					ZipEntry zipEntry = new ZipEntry("public.key");
					zipOut.putNextEntry(zipEntry);
					java.io.OutputStreamWriter pw = new java.io.OutputStreamWriter(zipOut);
					gcsr.writePublicKeyPEM(pw);
					pw.flush();
//					pw.close();
					//openssl rsa -pubin -noout -text -in /tmp/public.key
					zipEntry = new ZipEntry("private.key");
					zipOut.putNextEntry(zipEntry);
					pw = new java.io.OutputStreamWriter(zipOut);
					gcsr.writePrivateKeyPEM(pw, passwordf.getPassword());
					pw.flush();
//					pw.close();
					//openssl rsa -noout -text -in /tmp/secret.key

					zipEntry = new ZipEntry(openSSLConfParser.getId() + ".csr");
					zipOut.putNextEntry(zipEntry);
					pw = new java.io.OutputStreamWriter(zipOut);
					writeCSR(pw);
					pw.close();
					zipOut.close();
					fos.close();
					sendAction.setEnabled(true);
				}
				catch (java.lang.Throwable t)
				{
					de.elbosso.util.Utilities.handleException(null,t);
				}
				return true;
			}
		};

		action=new de.netsysit.util.pattern.command.ChooseFileAction(fp,null, de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/Save24.gif"));
		action.setAllowedSuffixes(new java.lang.String[]{"zip"});
		action.setCheckForExistingFilesWhenSaving(true);
		action.setSaveDialog(true);
		action.setParent(CSRBuilderGui.this);
		action.setDefaultFileEnding(".zip");
		sendAction=new javax.swing.AbstractAction("send",de.netsysit.util.ResourceLoader.getIcon("de/elbosso/ressources/gfx/tango/Mail-message_48.png"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
					java.io.PrintWriter pw = new java.io.PrintWriter(baos);
					X500Name x500Name=writeCSR(pw);
					pw.close();
					baos.close();
					PrepareEmailConfImpl mailConfig=new PrepareEmailConfImpl();
					mailConfig.setSubject(x500Name.toString());
					mailConfig.setContent(baos.toString());
					PrepareEmailAction act=new PrepareEmailAction(mailConfig);
					de.elbosso.util.Utilities.performAction(this,act);
				}
				catch(java.lang.Throwable t)
				{
					de.elbosso.util.Utilities.handleException(null,t);
				}
			}
		};
		sendAction.setEnabled(false);
		updateState();
	}
	private X500Name writeCSR(java.io.Writer pw) throws IOException, OperatorCreationException
	{
		X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);
		for (DnSpec spec : openSSLConfParser.getDnSpecs())
		{
			x500NameBld = x500NameBld.addRDN(spec.getStyle(), spec.getDef());
		}
		X500Name subject = x500NameBld.build();

		gcsr.writeCSRPEM(pw, subject, openSSLConfParser.generate(gcsr.getKeypair().getPublic()));
		return subject;
	}
	private void updateState()
	{
		boolean cond=false;
		cond=passwordf.getPassword().length>0;
		if(cond)
			cond=java.util.Arrays.equals(passwordf.getPassword(),verificationf.getPassword());

		if(fp!=null)
		{
			for (java.lang.String key : ruleMap.keySet())
			{
				javax.swing.JTextField tf = fp.fetchJTextField(key);
				de.netsysit.util.validator.Rule rule = ruleMap.get(key);
//				System.out.println(key+" "+rule);
				java.lang.String ts = de.netsysit.util.validator.Utilities.formatFailures(rule, tf.getText());
//				System.out.println(key+" "+rule+" "+tf.getText()+" "+ts);
				if (ts != null)
				{
					fp.decorateErrorProperty(key, ts);
					cond=false;
				}
				else
				{
					fp.decorateNothingProperty(key);
				}
			}
		}
		action.setEnabled(cond);
		if(action.isEnabled()==false)
			sendAction.setEnabled(false);
	}
	public static void main(String[] args) throws Exception
	{
//		de.elbosso.util.Utilities.configureBasicStdoutLogging(Level.ALL);
		try
		{
			java.util.Properties iconFallbacks = new java.util.Properties();
			java.io.InputStream is=de.netsysit.util.ResourceLoader.getResource("de/elbosso/ressources/data/icon_trans_material.properties").openStream();
			iconFallbacks.load(is);
			is.close();
			de.netsysit.util.ResourceLoader.configure(iconFallbacks);
		}
		catch(java.io.IOException ioexp)
		{
			ioexp.printStackTrace();
		}
//		de.netsysit.util.ResourceLoader.setSize(false ? de.netsysit.util.ResourceLoader.IconSize.medium : de.netsysit.util.ResourceLoader.IconSize.small);
		new CSRBuilderGui();
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		updateState();
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		updateState();
	}

	@Override
	public void changedUpdate(DocumentEvent e)
	{
		updateState();
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		if(e.getComponent()!=null)
		{
			if (javax.swing.JTextField.class.isAssignableFrom(e.getComponent().getClass()))
			{
				((javax.swing.JTextField) e.getComponent()).selectAll();
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e)
	{

	}
}
