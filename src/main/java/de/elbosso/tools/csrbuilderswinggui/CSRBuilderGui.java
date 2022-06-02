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

import de.elbosso.util.pattern.command.PrepareEmailAction;
import de.elbosso.util.pattern.command.PrepareEmailConfImpl;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.prefs.BackingStoreException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class CSRBuilderGui extends javax.swing.JFrame implements javax.swing.event.DocumentListener
	,java.awt.event.FocusListener
{
	private OpenSSLConfParser openSSLConfParser;
	private CSRBuilder gcsr;
	private de.netsysit.util.pattern.command.ChooseFileAction action;
	private javax.swing.Action sendAction;
	private javax.swing.Action buildP12Action;
	private javax.swing.JPasswordField passwordf;
	private javax.swing.JPasswordField verificationf;
	private java.util.Map<java.lang.String, de.netsysit.util.validator.Rule> ruleMap;
	private de.netsysit.ui.components.FormPanel formPanel;

	public CSRBuilderGui() throws IOException, BackingStoreException
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

			formPanel = new de.netsysit.ui.components.FormPanel(new java.awt.Insets(5, 0, 5, 0));
			javax.swing.JPasswordField pw = new javax.swing.JPasswordField();
			pw.getDocument().addDocumentListener(this);
			pw.addFocusListener(this);
			de.netsysit.util.validator.rules.MinMaxLengthRule rule = new de.netsysit.util.validator.rules.MinMaxLengthRule(6, Integer.MAX_VALUE);
			//		de.netsysit.util.validator.Utilities.hookupTextComponentWithRule(pw,rule);
			ruleMap.put("password", rule);
			passwordf = pw;
			formPanel.addRow("password", "password", pw);
			pw = new javax.swing.JPasswordField();
			pw.getDocument().addDocumentListener(this);
			pw.addFocusListener(this);
			//		de.netsysit.util.validator.Utilities.hookupTextComponentWithRule(pw,rule);
			ruleMap.put("verification", rule);
			verificationf = pw;
			formPanel.addRow("verification", "verification", pw);
			for (DnSpec spec : openSSLConfParser.getDnSpecs())
			{
				javax.swing.JTextField tf = new javax.swing.JTextField();
				tf.addFocusListener(this);
				formPanel.addRow(spec.getName(), spec.getName(), tf);
				tf.setText(spec.getDef());
				tf.getDocument().addDocumentListener(this);
				de.netsysit.util.validator.rules.MinMaxLengthRule mmrule = new de.netsysit.util.validator.rules.MinMaxLengthRule(spec.getMinChars(), spec.getMaxChars());
				//			de.netsysit.util.validator.Utilities.hookupTextComponentWithRule(tf,mmrule);
				ruleMap.put(spec.getName(), mmrule);
			}

			createActions();
			javax.swing.JPanel toplevel = new javax.swing.JPanel(new java.awt.BorderLayout());
			toplevel.add(formPanel);
			javax.swing.JToolBar tb = new javax.swing.JToolBar();
			tb.setFloatable(false);
			tb.add(action);
			tb.add(sendAction);
			tb.add(buildP12Action);
			toplevel.add(tb, BorderLayout.NORTH);
			setContentPane(toplevel);
			formPanel.setPreferredSize(new java.awt.Dimension(formPanel.getPreferredSize().width + 228, formPanel.getPreferredSize().height));
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
		buildP12Action=new AbstractAction(null,de.netsysit.util.ResourceLoader.getIcon("png/action/account_circle/materialicons/48dp/1x/baseline_account_circle_black_48dp.png"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					File archivefile=null;//new File("/home/elbosso/abc.zip");
					JFileChooser jfc=new JFileChooser();
					jfc.setSelectedFile(archivefile);
					jfc.setDialogTitle("Select archive containing private key!");
					if(jfc.showOpenDialog(formPanel)==jfc.CANCEL_OPTION)
						return;
					archivefile=jfc.getSelectedFile();

					File deliverablesfile=null;//new File("/home/elbosso/work/expect-dialog-ca.git/_priv/unpacked/deliverables_Amy Wong Kroker_2021-05-09_07-32-05.zip");
//					jfc.setSelectedFile(deliverablesfile);
					jfc.setDialogTitle("Select archive containing deliverables from the CA!");
					if(jfc.showOpenDialog(formPanel)==jfc.CANCEL_OPTION)
						return;
					deliverablesfile=jfc.getSelectedFile();

					File p12file=null;//new File("p12.p12");
//					jfc.setSelectedFile(p12file);
					jfc.setDialogTitle("Specify name for the resultinmg P12!");
					if(jfc.showSaveDialog(formPanel)==jfc.CANCEL_OPTION)
						return;
					p12file=jfc.getSelectedFile();

					javax.swing.JPasswordField pwf=new javax.swing.JPasswordField();
					pwf.setActionCommand("OK");
					pwf.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							if (e.getActionCommand() == "OK")
							{
								// https://stackoverflow.com/a/51356151/1020871
								SwingUtilities.getWindowAncestor(((JComponent) e.getSource())).dispose();
							}
						}
					});
					javax.swing.JOptionPane.showOptionDialog(null, pwf,
					"Enter Password",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.PLAIN_MESSAGE,
							null, new Object[0], null);

					ZipFile zipFile = new ZipFile(archivefile);

					Enumeration<? extends ZipEntry> entries = zipFile.entries();

					PrivateKey privateKey=null;
					X509Certificate cer=null;
					Collection<Certificate> c=null;

					while(entries.hasMoreElements()){
						ZipEntry entry = entries.nextElement();
						if(entry.getName().equals("private.key"))
						{

							InputStream stream = zipFile.getInputStream(entry);
							java.io.InputStreamReader isr=new InputStreamReader(stream);
							PEMParser keyReader = new PEMParser(isr);

							JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
							InputDecryptorProvider decryptionProv = new JceOpenSSLPKCS8DecryptorProviderBuilder().build(pwf.getPassword());

							org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo encrypted=(org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo)keyReader.readObject();
							PrivateKeyInfo keyInfo = encrypted.decryptPrivateKeyInfo(decryptionProv);
							privateKey = converter.getPrivateKey(keyInfo);

							isr.close();
							stream.close();
							break;
						}
					}

					zipFile = new ZipFile(deliverablesfile);

					entries = zipFile.entries();

					while(entries.hasMoreElements())
					{
						ZipEntry entry = entries.nextElement();
						if (entry.getName().endsWith("issuer.crt"))
						{
							CertificateFactory fact = CertificateFactory.getInstance("X.509");
							InputStream is = zipFile.getInputStream(entry);
							CertificateFactory cf = CertificateFactory.getInstance("X.509");
							c = (Collection<Certificate>)cf.generateCertificates(is);
						}
						else if (entry.getName().endsWith(".crt"))
						{

							CertificateFactory fact = CertificateFactory.getInstance("X.509");
							InputStream is = zipFile.getInputStream(entry);
							cer = (X509Certificate) fact.generateCertificate(is);
							PublicKey key = cer.getPublicKey();
							is.close();
						}
					}
					Certificate[] certs=new Certificate[c.size()+1];
					int i=0;
					certs[i]=cer;
					++i;
					for(Certificate cert:c)
					{
						certs[i]=cert;
						++i;
					}
					KeyStore ks = KeyStore.getInstance("PKCS12");
					ks.load(null, null);
					ks.setKeyEntry(cer.getIssuerDN().getName(), privateKey, pwf.getPassword(),
							certs);
					java.io.FileOutputStream fos=new java.io.FileOutputStream(p12file);
					ks.store(fos, pwf.getPassword());
					fos.close();
				}
				catch(java.lang.Throwable t)
				{
					de.elbosso.util.Utilities.handleException(null,t);
				}
			}
		};
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
		{
			cond = java.util.Arrays.equals(passwordf.getPassword(), verificationf.getPassword());
		}

		if(formPanel !=null)
		{
			for (java.lang.String key : ruleMap.keySet())
			{
				javax.swing.JTextField tf = formPanel.fetchJTextField(key);
				de.netsysit.util.validator.Rule rule = ruleMap.get(key);
//				System.out.println(key+" "+rule);
				java.lang.String ts = de.netsysit.util.validator.Utilities.formatFailures(rule, tf.getText());
//				System.out.println(key+" "+rule+" "+tf.getText()+" "+ts);
				if (ts != null)
				{
					formPanel.decorateErrorProperty(key, ts);
					cond=false;
				}
				else
				{
					formPanel.decorateNothingProperty(key);
				}
			}
		}
		if(passwordf.getPassword().length>0)
		{
			if(java.util.Arrays.equals(passwordf.getPassword(), verificationf.getPassword())==false)
			{
				formPanel.decorateErrorProperty("verification", "Passwords do not match!");
			}
		}

		action.setEnabled(cond);
		if(action.isEnabled()==false)
			sendAction.setEnabled(false);
	}
	public static void main(String[] args) throws Exception
	{
		Security.addProvider(new BouncyCastleProvider());
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
