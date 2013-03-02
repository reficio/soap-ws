#!/bin/bash
keytool -genkey -keystore .keystore -alias john -storepass changeit -keypass changeit -dname "CN=John Smith, OU=Development, O=Standard Supplies Inc., L=Anytown, S=North Carolina, C=US"
keytool -genkey -keystore .keystore -alias george -storepass changeit -keypass changeit -dname "CN=George Kowalski, OU=Development, O=Standard Supplies Inc., L=Anytown, S=North Carolina, C=US"
