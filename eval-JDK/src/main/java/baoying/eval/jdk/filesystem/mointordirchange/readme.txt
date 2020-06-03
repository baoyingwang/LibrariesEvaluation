requirement:
a log file is keep changing by appending.
once it print a line with "catch me", it should be notified to operator.

caution: the file maybe would be hundred MB.
ext. the log file would roll over day by day
ext. monitor all logs under a specifc directory


========
solution A: recommended, use unix/linux script

A1
tail -f xx.log | grep .... | ...
http://forums.asmallorange.com/topic/13649-shell-script-to-monitor-file-changes/

A2
inotify-tools is a C library and a set of command-line programs for Linux providing a simple interface to inotify. 
http://nix-tips.blogspot.com/2009/08/monitor-file-changes-in-shell-script.html
???
inotifywait -m -r --format '%f' -e modify -e move -e create -e delete ~/test | while read line
do
	echo "hello $line"
done
£¿£¿£¿
========
solution B: recommended, use perl
????
http://www.nooblet.org/blog/2008/directory_monitorpl-monitor-directory-for-file-changes/
========
solutio C: java

1.1 monitor the file changed by ApacheCommoIOMonitor
    
    a. record the total line number for each modification.
    b. get the line
    
    
 