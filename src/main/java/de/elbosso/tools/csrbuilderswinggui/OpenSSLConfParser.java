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

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OpenSSLConfParser extends java.lang.Object
{
	private static java.util.Map<java.lang.String,java.lang.Integer>openSSLKeyUsages=new java.util.HashMap();
	private static java.util.Map<java.lang.String,KeyPurposeId>openSSLExtendedKeyUsages=new java.util.HashMap();

	static
	{
		//https://www.openssl.org/docs/manmaster/man5/x509v3_config.html
		// digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment, keyAgreement, keyCertSign,
		// cRLSign, encipherOnly and decipherOnly
		openSSLKeyUsages.put("digitalSignature",KeyUsage.digitalSignature);
		openSSLKeyUsages.put("nonRepudiation",KeyUsage.nonRepudiation);
		openSSLKeyUsages.put("keyEncipherment",KeyUsage.keyEncipherment);
		openSSLKeyUsages.put("dataEncipherment",KeyUsage.dataEncipherment);
		openSSLKeyUsages.put("keyAgreement",KeyUsage.keyAgreement);
		openSSLKeyUsages.put("keyCertSign",KeyUsage.keyCertSign);
		openSSLKeyUsages.put("cRLSign",KeyUsage.cRLSign);
		openSSLKeyUsages.put("encipherOnly",KeyUsage.encipherOnly);
		openSSLKeyUsages.put("decipherOnly",KeyUsage.decipherOnly);
		//https://www.openssl.org/docs/manmaster/man5/x509v3_config.html
		// serverAuth             SSL/TLS Web Server Authentication.
		// clientAuth             SSL/TLS Web Client Authentication.
		// codeSigning            Code signing.
		// emailProtection        E-mail Protection (S/MIME).
		// timeStamping           Trusted Timestamping
		// OCSPSigning            OCSP Signing
		// ipsecIKE               ipsec Internet Key Exchange
		// msCodeInd              Microsoft Individual Code Signing (authenticode)
		// msCodeCom              Microsoft Commercial Code Signing (authenticode)
		// msCTLSign              Microsoft Trust List Signing
		// msEFS                  Microsoft Encrypted File System
		openSSLExtendedKeyUsages.put("serverAuth",KeyPurposeId.id_kp_serverAuth);
		openSSLExtendedKeyUsages.put("clientAuth",KeyPurposeId.id_kp_clientAuth);
		openSSLExtendedKeyUsages.put("codeSigning",KeyPurposeId.id_kp_codeSigning);
		openSSLExtendedKeyUsages.put("emailProtection",KeyPurposeId.id_kp_emailProtection);
		openSSLExtendedKeyUsages.put("timeStamping",KeyPurposeId.id_kp_timeStamping);
		openSSLExtendedKeyUsages.put("OCSPSigning",KeyPurposeId.id_kp_OCSPSigning);
		openSSLExtendedKeyUsages.put("ipsecIKE",KeyPurposeId.id_kp_ipsecIKE);
		//https://ldapwiki.com/wiki/ExtendedKeyUsage
		openSSLExtendedKeyUsages.put("msCodeInd",KeyPurposeId.getInstance(new ASN1ObjectIdentifier("1.3.6.1.4.1.311.2.1.21")));
		openSSLExtendedKeyUsages.put("msCodeCom",KeyPurposeId.getInstance(new ASN1ObjectIdentifier("1.3.6.1.4.1.311.2.1.22")));
		openSSLExtendedKeyUsages.put("msCTLSign",KeyPurposeId.getInstance(new ASN1ObjectIdentifier("1.3.6.1.4.1.311.10.3.1")));
		openSSLExtendedKeyUsages.put("msEFS",KeyPurposeId.getInstance(new ASN1ObjectIdentifier("1.3.6.1.4.1.311.10.3.4")));
	}

	private int keyLengthInBits;
	private java.util.LinkedList<DnSpec> dnSpecs;
	private ExtensionsGenerator extGen = new ExtensionsGenerator();
	private java.lang.String id;
	private java.util.prefs.Preferences extensionsNode;
	private java.util.prefs.Preferences dnNode;

	public OpenSSLConfParser(java.net.URL url) throws java.io.IOException
	{
		super();
		init(new org.ini4j.IniPreferences(url));
	}
	public OpenSSLConfParser(java.io.Reader reader) throws java.io.IOException
	{
		super();
		init(new org.ini4j.IniPreferences(reader));
	}
	public OpenSSLConfParser(java.io.InputStream in) throws java.io.IOException
	{
		super();
		init(new org.ini4j.IniPreferences(in));
	}
	private void init(java.util.prefs.Preferences prefs) throws IOException
	{
		java.util.prefs.Preferences reqNode=prefs.node("req");
		extensionsNode=prefs.node(get(reqNode,"req_extensions",null));
		dnNode=prefs.node(get(reqNode,"distinguished_name",null));
		keyLengthInBits=getInt(reqNode,"default_bits",4096);
		dnSpecs=new java.util.LinkedList();
		for(DnSpec spec: DnSpec.values())
		{
//			System.out.println(spec.getName());
			if(get(dnNode,spec.getName(),"-").equals("-")==false)

			{
				dnSpecs.add(spec);
				spec.setDef(unquote(get(dnNode,spec.getName()+"_default","")));
				spec.setMaxChars(getInt(dnNode,spec.getName()+"_max", Integer.MAX_VALUE));
			}
		}
		id=get(reqNode,"distinguished_name",null);

	}
	public Extensions generate(java.security.PublicKey publicKey) throws IOException, OperatorCreationException
	{
		java.lang.String keyUsages=get(extensionsNode,"keyUsage","-");
		if(keyUsages.equals("-")==false)
		{
			int mask=0;
			boolean critical=false;
			java.util.StringTokenizer tok=new java.util.StringTokenizer(keyUsages,",",false);
			while(tok.hasMoreTokens())
			{
				java.lang.String token=tok.nextToken().trim();
				critical=critical||token.equals("critical");
				if(openSSLKeyUsages.containsKey(token))
				{
					mask|=openSSLKeyUsages.get(token);
				}
			}
			if(mask!=0)
			{
				Extension keyUsage =
						new Extension(Extension.keyUsage, critical,
								new DEROctetString(new KeyUsage(mask)));
				extGen.addExtension(keyUsage);
			}
		}
		java.lang.String extendedKeyUsages=get(extensionsNode,"extendedKeyUsage","-");
		if(extendedKeyUsages.equals("-")==false)
		{
			java.util.List<KeyPurposeId> keyPurposeIds=new java.util.LinkedList();
			boolean critical=false;
			java.util.StringTokenizer tok=new java.util.StringTokenizer(extendedKeyUsages,",",false);
			while(tok.hasMoreTokens())
			{
				java.lang.String token=tok.nextToken().trim();
				critical=critical||token.equals("critical");
				if(openSSLExtendedKeyUsages.containsKey(token))
				{
					keyPurposeIds.add(openSSLExtendedKeyUsages.get(token));
				}
			}
			if(keyPurposeIds.isEmpty()==false)
			{
				Extension extendedKeyUsage =
						new Extension(Extension.extendedKeyUsage, critical,
								new DEROctetString(new ExtendedKeyUsage(keyPurposeIds.toArray(new KeyPurposeId[0]))));
				extGen.addExtension(extendedKeyUsage);
			}
		}
		java.lang.String subjectKeyIdentifierExtendsion=get(extensionsNode,"subjectKeyIdentifier","-");
		if(subjectKeyIdentifierExtendsion.contains("hash")==true)
		{
			DigestCalculator digCalc = new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
			X509ExtensionUtils x509ExtensionUtils = new X509ExtensionUtils(digCalc);
			Extension subjectKeyIdentifier =
					new Extension(Extension.subjectKeyIdentifier, subjectKeyIdentifierExtendsion.contains("critical"),
							new DEROctetString(x509ExtensionUtils.createSubjectKeyIdentifier(new SubjectPublicKeyInfo(ASN1Sequence.getInstance(publicKey.getEncoded())))));
			extGen.addExtension(subjectKeyIdentifier);
		}
		java.lang.String subjectAltNameExtension=get(extensionsNode,"subjectAltName","-");
		if(subjectAltNameExtension.equals("-")==false)
		{
			/*
			 * Add SubjectAlternativeNames (SANs)
			 */
			if(extendedKeyUsages.contains("serverAuth"))
			{
				List<GeneralName> namesList = new ArrayList<>();
				GeneralName gn = new GeneralName(GeneralName.dNSName, "example.org");
				namesList.add(gn);
				GeneralNames subjectAltNames = new GeneralNames(namesList.toArray(new GeneralName[]{}));
				extGen.addExtension(Extension.subjectAlternativeName, subjectAltNameExtension.contains("critical"), subjectAltNames);
			}
			else
			{
				if(subjectAltNameExtension.contains("email:move")==true)
				{
					if (get(dnNode, "emailAddress", "-").equals("-") == false)
					{
						List<GeneralName> namesList = new ArrayList<>();
						GeneralName gn = new GeneralName(GeneralName.rfc822Name, get(dnNode, "emailAddress_default", ""));
						namesList.add(gn);
						GeneralNames subjectAltNames = new GeneralNames(namesList.toArray(new GeneralName[]{}));
						extGen.addExtension(Extension.subjectAlternativeName, subjectAltNameExtension.contains("critical"), subjectAltNames);
					}
				}
			}
		}
		return extGen.generate();
	}

	public int getKeyLengthInBits()
	{
		return keyLengthInBits;
	}

	public LinkedList<DnSpec> getDnSpecs()
	{
		return dnSpecs;
	}

	public String getId()
	{
		return id;
	}

	private int getInt(java.util.prefs.Preferences prefs, java.lang.String key, int def)
	{
		java.lang.String str=prefs.get(key,java.lang.Integer.toString(def));
		int index=str.indexOf('#');
		if(index>-1)
			str=str.substring(0,index).trim();
		return java.lang.Integer.parseInt(str);
	}
	private java.lang.String get(java.util.prefs.Preferences prefs,java.lang.String key,java.lang.String def)
	{
		java.lang.String str=prefs.get(key,def);
		int index=str.indexOf('#');
		if(index>-1)
			str=str.substring(0,index).trim();
		return str;
	}
	private java.lang.String unquote(java.lang.String quoted)
	{
		quoted=quoted.trim();
		if(quoted.startsWith("\""))
			quoted=quoted.substring(1);
		if(quoted.endsWith("\""))
			quoted=quoted.substring(0,quoted.length()-1);
		return quoted;
	}
}
