To get all the dependencies:
navigate to the Dodecathedral folder
git submodule update --init

navigate to dependencied/pd-for-android
git submodule update --init

For Dodecathedral-win, the libpd dlls need to be added to a folder that is on the PATH. Copy them from:
Dodecathedral\dependencies\lipd-windows-build\java-build\org\puredata\core\natives\windows\x86_64

Dodecathedral-Droid dependencies:
	dodecathedral-core
		dependencies\processing-android\core

	dodecathedral-droid
		dependencies\processing-android\core
		dependencies\pd-for-android\PdCore


--one day there will be a gradle config for all this