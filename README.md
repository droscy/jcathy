# jCathy
Catalog for files and folders of removable devices.

## License
jCathy v0.7.5+dev<br/>
Copyright (C) 2007-2014 Simone Rossetto <simros85@gmail.com><br/>
GNU General Public License v3

    jCathy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    Thermod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.


## Description
jCathy creates a catalog of files and folders of removable devices
(or folder in the system) and stores them in a database, assigning a
unique identifier (choosen by the user) to that volume. This database
can be searched by file/folder name to get the name of the volume which
that file/folder belongs to, so users can easily locate theirs files.

## Building and Installing
To build jCathy you need [Java SE JDK v1.6+](http://www.oracle.com/technetwork/java/index.html)
and [Apache Ant](http://ant.apache.org/) installed on your system.

Once done open a shell terminal (like DOS Command Prompt on Windows, or
Konsole on GNU/Linux) move to the folder containing the source tree of
jCathy and simply execute `ant`.

After a successfull building, create the subfolder `lib` in the same folder
of `jcathy.jar` and copy in there the file `hsqldb.jar` downloaded from the
[HyperSQL v2.3.2](http://hsqldb.org/) website. On Windows you can simply
double-click on `jcathy.jar` to start the program, on GNU/Linux edit
the file `jcathy` on line 28, set the path to your `jcathy.jar` file and
execute it with `./jcathy`, that file can be copied to `/usr/bin` for a
simpler command-line execution.

To install jCathy move `jcathy`, `jcathy.jar` files and `lib` folder
(with its content) wherever you want in the system. The actual database will
be created in the same folder with name `jcathy.db.properties`
and `jcathy.db.script`.

### Uninstalling
Removes the folder (with content) where yiu have installed jCathy. Both program
and database will be deleted.

### Source package
To create the source package of jCathy, you can execute `ant source`.
The file `jcathy-${version}-src.tar.gz` will be created.

### Note for Debian-based systems
A Debian package can be build using
[git-buildpackage](https://honk.sigxcpu.org/piki/projects/git-buildpackage/).

Assuming you have already configured your system to use git-buildpackage
(if not see Debian Wiki for [git-pbuilder](https://wiki.debian.org/git-pbuilder),
[cowbuilder](https://wiki.debian.org/cowbuilder),
[Packaging with Git](https://wiki.debian.org/PackagingWithGit) and
[Using Git for Debian Packaging](https://www.eyrie.org/~eagle/notes/debian/git.html))
then these are the basic steps:

```bash
git clone https://github.com/droscy/jcathy.git
cd jcathy
git branch --track pristine-tar origin/pristine-tar
git checkout -b debian/master origin/debian/master
gbp buildpackage
```

The packages can then be installed as usual:

```bash
dpkg -i jcathy_{version}_all.deb
```

## Usage
**Add a Volume:**
  Open jCathy, go to the *Catalog* tab select the source root to be catalogued
  by clicking the *Browse* button. Then choose a name for this volume and
  click on *Add* button. Do this for each volume you want to store.
  The *Ignore* field can be used to ignore some files/folder whose name
  matches the pattern: these files/folder will not be indicized.

**Search files/folders:**
  Open jCathy, go to the *Search* tab, enter the pattern to be searched (you
  can use the asterisk * as a jolly character) and click on *Search*. The list
  of files/folders with the choosen name will be listed. Click on one of them
  and go to the *Explore* tab, there you can navigate the whole tree for that
  volume.

## Changelog
* v0.7.6 (under development)

  - versioning system migrated to git
  - separate branch for `debian/` folder to use git-buildpackage
  - upgrade startup script with java version checking

* v0.7.5 [2014-04-20]

  - switched to HSQLDB v2.x

* v0.7.4 [2011-02-24]

  - Added this README and this Changelog
  - Created debian subfolder to build and install this program in
  - Debian-based systems
  - Corrected mistakes in English words
  - Created application icons and Desktop Entry for GNU/Linux desktop
  - environments
  - Released under GPLv3

* v0.7.3 [2010-08-15]

  - Volumes can now be renamed

* v0.7.2 [2010-07-25]

  - First release
