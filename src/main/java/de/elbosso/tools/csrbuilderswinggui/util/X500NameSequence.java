/*
 * Copyright (c) 2019.
 *
 * Juergen Key. Alle Rechte vorbehalten.
 *
 * Weiterverbreitung und Verwendung in nichtkompilierter oder kompilierter Form,
 * mit oder ohne Veraenderung, sind unter den folgenden Bedingungen zulaessig:
 *
 *1. Weiterverbreitete nichtkompilierte Exemplare muessen das obige Copyright,
 * die Liste der Bedingungen und den folgenden Haftungsausschluss im Quelltext
 * enthalten.
 *2. Weiterverbreitete kompilierte Exemplare muessen das obige Copyright,
 * die Liste der Bedingungen und den folgenden Haftungsausschluss in der
 * Dokumentation und/oder anderen Materialien, die mit dem Exemplar verbreitet
 * werden, enthalten.
 *3. Weder der Name des Autors noch die Namen der Beitragsleistenden
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

package de.elbosso.tools.csrbuilderswinggui.util;

import de.elbosso.tools.csrbuilderswinggui.DnSpec;
import de.elbosso.util.generator.semantics.LandmarkNameSequence;
import de.elbosso.util.generator.semantics.NameEMailAddressBundleSequence;
import de.elbosso.util.generator.semantics.RandomCountryCodeSequence;
import de.netsysit.util.lang.Tupel;
import org.apache.log4j.Level;

import java.security.Principal;

public class X500NameSequence extends Object implements de.netsysit.util.generator.Sequence<org.bouncycastle.asn1.x500.X500Name >
{
	private final static org.apache.log4j.Logger CLASS_LOGGER = org.apache.log4j.Logger.getLogger(X500NameSequence.class);
	private final static java.util.ResourceBundle i18n=java.util.ResourceBundle.getBundle("de.netsysit.util.i18n",java.util.Locale.getDefault());
	private boolean allowsNull;
	private final de.netsysit.util.generator.generalpurpose.BiasedBooleanSequence doublebool;
	private final NameEMailAddressBundleSequence nameEMailAddressBundleSequence;
	private final de.elbosso.util.generator.semantics.FamilyBusinessSequence domainseq;
	private final de.netsysit.util.generator.semantics.PlacesSequence citySeq;
	private final RandomCountryCodeSequence countryCodeSeq;
	private final LandmarkNameSequence landmarkNameSequence;
//	private final de.netsysit.util.generator.semantics.HausNummerSequence hausNummerSequence;
//	private final de.netsysit.util.generator.semantics.AddressSequence addressSequence;
	private de.netsysit.util.generator.generalpurpose.RandomStringFromClosedSetSequence d;
	private java.lang.String country="DE";
	private boolean includeEMail;

	public X500NameSequence()
	{
		super();
		nameEMailAddressBundleSequence=new NameEMailAddressBundleSequence();
		nameEMailAddressBundleSequence.setAllowsNull(false);
		doublebool=new de.netsysit.util.generator.generalpurpose.BiasedBooleanSequence();
		doublebool.setThreshold(0.9);
		domainseq=new de.elbosso.util.generator.semantics.FamilyBusinessSequence();
		domainseq.setAllowsNull(false);
		citySeq=new de.netsysit.util.generator.semantics.PlacesSequence();
		citySeq.setAllowsNull(false);
		countryCodeSeq=new RandomCountryCodeSequence();
		countryCodeSeq.setAllowsNulls(false);
		landmarkNameSequence=new LandmarkNameSequence();
/*		hausNummerSequence=new de.netsysit.util.generator.semantics.HausNummerSequence();
		hausNummerSequence.setMax(1000);
		hausNummerSequence.setFrom('a');
		hausNummerSequence.setTo('d');
		hausNummerSequence.setAppartments(true);
		hausNummerSequence.setAllowsNull(false);
		addressSequence=new de.netsysit.util.generator.semantics.AddressSequence();
		addressSequence.setAllowsNull(false);
*/		d=new de.netsysit.util.generator.generalpurpose.RandomStringFromClosedSetSequence();
		d.setObjectsToChoosefrom(new String[]{
				"Baden-Württemberg",
						"Bayern",
						"Berlin",
						"Brandenburg",
						"Bremen",
						"Hamburg",
						"Hessen",
						"Mecklenburg-Vorpommern",
						"Niedersachsen",
						"Nordrhein-Westfalen",
						"Rheinland-Pfalz",
						"Saarland",
						"Sachsen-Anhalt",
						"Sachsen",
						"Schleswig-Holstein",
						"Thüringen"
		});
	}

	public void setIncludeEMail(boolean includeEMail)
	{
		boolean old = isIncludeEMail();
		this.includeEMail = includeEMail;
//		send("includeEMail", old, isIncludeEMail());
	}

	public boolean isIncludeEMail()
	{
		return includeEMail;
	}

	public void setCountry(String country)
	{
		String old = getCountry();
		this.country = country;
//		send("country", old, getCountry());
	}

	public String getCountry()
	{
		return country;
	}

	public boolean hasNext()
	{
		return true;
	}

	@Override
	public org.bouncycastle.asn1.x500.X500Name next()
	{
		org.bouncycastle.asn1.x500.X500Name rv=null;
		if((isAllowsNull())&&(doublebool.next().booleanValue()))
			rv=null;
		else
		{
			java.util.LinkedList<DnSpec> dnSpecs=new java.util.LinkedList();
			Tupel<String,String> tupel=nameEMailAddressBundleSequence.next();
			DnSpec.CountryName.setDef(massagePart(country!=null?country:countryCodeSeq.next()));
			dnSpecs.add(DnSpec.CountryName);
			DnSpec.StateOrProvince.setDef(massagePart(d.next()));
			dnSpecs.add(DnSpec.StateOrProvince);
			DnSpec.Locality.setDef(massagePart(citySeq.next()));
			dnSpecs.add(DnSpec.Locality);
//			java.lang.String s=addressSequence.next()+" "+hausNummerSequence.next()
//			DnSpec..setDef(s);
			DnSpec.Organization.setDef(massagePart(domainseq.next()));
			dnSpecs.add(DnSpec.Organization);
			DnSpec.OrganizationalUnit.setDef(massagePart(landmarkNameSequence.next()));
			dnSpecs.add(DnSpec.OrganizationalUnit);
			DnSpec.CN.setDef(massagePart(tupel.getLefty()));
			dnSpecs.add(DnSpec.CN);
			if(isIncludeEMail())
			{
				DnSpec.EMail.setDef(tupel.getRighty());
				dnSpecs.add(DnSpec.EMail);
			}
			org.bouncycastle.asn1.x500.X500NameBuilder x500NameBld = new org.bouncycastle.asn1.x500.X500NameBuilder(org.bouncycastle.asn1.x500.style.BCStyle.INSTANCE);
			for (DnSpec spec : dnSpecs)
			{
				x500NameBld = x500NameBld.addRDN(spec.getStyle(), spec.getDef());
			}
			org.bouncycastle.asn1.x500.X500Name subject = x500NameBld.build();
			rv=subject;
		}
		return rv;
	}

	private java.lang.String massagePart(java.lang.String s)
	{
		return s.replace(",", "").replace("Ö","OE").replace("Ü","UE").replace("Ä","AE").replace("ü","ue").replace("ö","oe").replace("ä","ae").replace("ß","ss");
	}

	public void remove()
	{
		throw new UnsupportedOperationException("Not supported .");
	}
	public boolean isAllowsNull()
	{
		return allowsNull;
	}

	public void setAllowsNull(boolean allowsNull)
	{
		this.allowsNull = allowsNull;
	}
	@Override
	public String toString()
	{
		String rv=this.getClass().getSimpleName();
//		rv=i18n.getString(".name");
		return rv;
	}
	public static void main (String[] args)
	{
		de.elbosso.util.Utilities.configureBasicStdoutLogging(Level.ALL);
		X500NameSequence tis=new X500NameSequence();
		tis.setIncludeEMail(true);
		for(int i=0;i<30;++i)
		{
			if (CLASS_LOGGER.isDebugEnabled()) CLASS_LOGGER.debug(tis.next());
		}
	}
}
