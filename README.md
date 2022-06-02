# CSRBuilderGui

<!---
[![start with why](https://img.shields.io/badge/start%20with-why%3F-brightgreen.svg?style=flat)](http://www.ted.com/talks/simon_sinek_how_great_leaders_inspire_action)
--->
[![GitHub release](https://img.shields.io/github/release/elbosso/csrbuilderswinggui/all.svg?maxAge=1)](https://GitHub.com/elbosso/csrbuilderswinggui/releases/)
[![GitHub tag](https://img.shields.io/github/tag/elbosso/csrbuilderswinggui.svg)](https://GitHub.com/elbosso/csrbuilderswinggui/tags/)
[![GitHub license](https://img.shields.io/github/license/elbosso/csrbuilderswinggui.svg)](https://github.com/elbosso/csrbuilderswinggui/blob/master/LICENSE)
[![GitHub issues](https://img.shields.io/github/issues/elbosso/csrbuilderswinggui.svg)](https://GitHub.com/elbosso/csrbuilderswinggui/issues/)
[![GitHub issues-closed](https://img.shields.io/github/issues-closed/elbosso/csrbuilderswinggui.svg)](https://GitHub.com/elbosso/csrbuilderswinggui/issues?q=is%3Aissue+is%3Aclosed)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/elbosso/csrbuilderswinggui/issues)
[![GitHub contributors](https://img.shields.io/github/contributors/elbosso/csrbuilderswinggui.svg)](https://GitHub.com/elbosso/csrbuilderswinggui/graphs/contributors/)
[![Github All Releases](https://img.shields.io/github/downloads/elbosso/csrbuilderswinggui/total.svg)](https://github.com/elbosso/csrbuilderswinggui)
[![Website elbosso.github.io](https://img.shields.io/website-up-down-green-red/https/elbosso.github.io.svg)](https://elbosso.github.io/)

This project is meant to pose as an alternative way to create Certificate signing
requests. It is a sister project to [expect-dialog-ca](https://github.com/elbosso/expect-dialog-ca).
The optimal way for a CA to endow someone with a digital identity is to 
take a Certificate Signing Request (CSR) and to generate a digital certificate 
for it. The user then can combine this with its private key and as a result 
has a digital identity.

Many enrollment processes nowadays work differently: The CA creates both the private key and the public
key and somehow send them with the certificate to the end user. But that means 
that the private key is no longer private: the CA must know the password used
to secure the private key - both for creating the CSR ands of course for supplying it
to the end user.

It would be best if the private key stays with the end user. That however
means that the end user must be able to create the private key and the CSR 
without any hustle because that would destroy the acceptance.

There are some (in fact: many) web portals offering the creation of CSRs
along with private keys by filling in a small form with the needed information.
This seems alluring - but it also means, that the private key is not really private:
Either the password for the private key has to be sent over the internet or 
the private key is created without any password protection - both alternatives 
are (in my opinion) equally bad.

That are the reasons for the inception of this project: A small application that
can be executed locally. It works in two phases (the second one being optional) 
consisting of the following steps:

* Loading an OpenSSL configuration for the kind of certificate to be obtained
* Presenting a form to the user asking the needed information according to this configuration
* Creating a private key and a CSR
* Saving public and private keys and CSR in a Zip archive
* Opening the systems mail application with the CSR as mail body to be sent to the CA

The second phase begins once the CA has sent the certificate back:

* combination of the public and private key with the certificate to create
a digital identity in the form of a PKCS#12 container.

The form that is presented for the user to fill out does ask questions
about the individual RDNs making up the CN for the certificate. Currently, the
following RDNs are supported - if present in the used configuration file:

* CountryName
* StateOrProvince
* Locality
* Organization
* OrganizationalUnit
* SurName
* SerialNumber
* StreetAddress
* Title 
* GivenName
* UserID
* DomainComponent
* CommonName
* EMailAddress

## Build

The project can be built and executed using this command

```
mvn compile exec:java
```

## Distribution

It is possible to create a single jar with all dependencies included that can be distributed
to prospective users (provided they have a java runtime environment installed):

```
mvn -U package assembly:single
```

Alternatively, it is possible to distribute the solution along with a bundled
runtime environment. This still needs a bit of manual labor at this time. The whole
process is described 
[here.](https://medium.com/azulsystems/using-jlink-to-build-java-runtimes-for-non-modular-applications-9568c5e70ef4)
