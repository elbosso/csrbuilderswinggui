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

import java.io.IOException;
import java.io.StringWriter;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
// not working! https://www.journaldev.com/223/java-generate-csr-program
//https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.sec.doc/q009860_.htm
//https://stackoverflow.com/questions/34169954/create-pkcs10-request-with-subject-alternatives-using-bouncy-castle-in-java
//https://www.codota.com/code/java/classes/org.bouncycastle.asn1.x509.GeneralName
//https://www.programcreek.com/java-api-examples/?api=org.bouncycastle.asn1.x509.KeyUsage
//https://www.programcreek.com/java-api-examples/index.php?api=org.bouncycastle.cert.X509ExtensionUtils
//https://www.javatips.net/api/org.bouncycastle.cert.x509extensionutils

public class CSRBuilder {
	private KeyPairGenerator keyGen;
	private KeyPair keypair;

	CSRBuilder(int keylengthInBits) throws IOException
	{
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyGen.initialize(keylengthInBits, new SecureRandom());
		keypair = keyGen.generateKeyPair();
	}

	public KeyPair getKeypair()
	{
		return keypair;
	}

	public void writePublicKeyPEM(java.io.Writer w) throws IOException
	{
		w.write("-----BEGIN PUBLIC KEY-----\n");
		w.write (java.util.Base64.getMimeEncoder().encodeToString( keypair.getPublic().getEncoded()));
		w.write ("\n----END PUBLIC KEY-----\n");
	}

	public void writePrivateKeyPEM(java.io.Writer w,char[] password) throws IOException, OperatorCreationException
	{
		// construct encryptor builder to encrypt the private key
		// provider is needed for the encryptor builder
		Security.addProvider(new BouncyCastleProvider());
		JceOpenSSLPKCS8EncryptorBuilder encryptorBuilder = new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.AES_256_CBC);
		encryptorBuilder.setRandom(new SecureRandom());
		encryptorBuilder.setPasssword("password".toCharArray());
		OutputEncryptor encryptor = encryptorBuilder.build();

		// construct object to create the PKCS8 object from the private key and encryptor
		JcaPKCS8Generator pkcsGenerator = new JcaPKCS8Generator(keypair.getPrivate(), encryptor);
		PemObject pemObj = pkcsGenerator.generate();
		StringWriter stringWriter = new StringWriter();
		try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
			pemWriter.writeObject(pemObj);
		}

		// write PKCS8 to file
		w.write(stringWriter.toString()+"\n");
	}

	public void writeCSRPEM(java.io.Writer w,X500Name subject, Extensions extensions) throws OperatorCreationException, IOException
	{
		PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
				subject, keypair.getPublic());
		JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("sha256withRSA");
		ContentSigner signer = csBuilder.build(keypair.getPrivate());
		/*
		 * Use ExtensionsGenerator to add individual extensions.
		 */
		p10Builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extensions);

		PKCS10CertificationRequest csr = p10Builder.build(signer);
		JcaPEMWriter writer=new JcaPEMWriter(w);
		writer.writeObject(csr);
		writer.close();
	}

}