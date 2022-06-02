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
import org.bouncycastle.asn1.x500.style.BCStyle;

public enum DnSpec
{
	CountryName("countryName", BCStyle.C),
	StateOrProvince("stateOrProvinceName",BCStyle.ST),
	Locality("localityName",BCStyle.L),
	Organization("organizationName",BCStyle.O),
	OrganizationalUnit("organizationalUnitName",BCStyle.OU),
	SurName("surname",BCStyle.SURNAME),
	SerialNumber("serialNumber",BCStyle.SERIALNUMBER),
	StreetAddress("streetAddress",BCStyle.STREET),
	Title("title",BCStyle.T),
	GivenName("givenName",BCStyle.GIVENNAME),
	UserID("userID",BCStyle.UID),
	DomainComponent("domainComponent",BCStyle.DC),
//	("",BCStyle.),
//	("",BCStyle.),
	CN("commonName",BCStyle.CN),
	EMail("emailAddress",BCStyle.EmailAddress);

	private String name;
	private int maxChars= Integer.MAX_VALUE;
	private int minChars;
	private String def="";
	private ASN1ObjectIdentifier style;

	DnSpec(String name, ASN1ObjectIdentifier style)
	{
		this.name=name;
		this.style=style;
	}

	public ASN1ObjectIdentifier getStyle()
	{
		return style;
	}

	public String getName()
	{
		return name;
	}

	public void setDef(String def)
	{
		String old = getDef();
		this.def = def;
//			send("def", old, getDef());
	}

	public String getDef()
	{
		return def;
	}

	public void setMaxChars(int maxChars)
	{
		int old = getMaxChars();
		this.maxChars = maxChars;
//			send("maxChars", old, getMaxChars());
	}

	public int getMaxChars()
	{
		return maxChars;
	}

	public void setMinChars(int minChars)
	{
		int old = getMinChars();
		this.minChars = minChars;
//			send("minChars", old, getMinChars());
	}

	public int getMinChars()
	{
		return minChars;
	}

	@Override
	public String toString()
	{
		return getName()+"::"+getDef()+"--"+getMaxChars();
	}
}
