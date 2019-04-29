@echo off
@echo ============================================================
@echo This will save all current data to the backup directory, and 
@echo restore the backup from April 5 2010
@echo Please make sure that the Template Server Program has 
@echo been stopped.
@echo ============================================================
@echo ... 1 of 3: please hit enter to continue, or [ctrl]C to break and exit ...
pause
@echo ... 2 of 3: please hit enter to continue, or [ctrl]C to break and exit ...
pause
@echo ... 3 of 3: please hit enter to continue, or [ctrl]C to break and exit ...
pause

mkdir backup
mkdir backup\database
copy lib\oa.jar backup
copy lib\template.jar backup

xcopy database backup\database /s/y/q

xcopy backup_20100405 . /s/y/q
move *.jar lib /y
@echo Restore has completed
pause
