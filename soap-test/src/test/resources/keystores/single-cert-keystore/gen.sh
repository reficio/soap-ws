#!/bin/bash
keytool -genkey -keystore .keystore -alias key01 -storepass johnstorepass -keypass johnstorepass -dname "CN=John Smith, OU=Development, O=Standard Supplies Inc., L=Anytown, S=North Carolina, C=US"
