#!/usr/bin/expect -f

#set port 22
#set user urrp
set user [lindex $argv 0]
set password [lindex $argv 1]
set host [lindex $argv 2]
set v_RemoteFileName [lindex $argv 3]
set v_changeSFileName [lindex $argv 4]



#set host 10.1.244.73
#set host 10.200.250.25
#set password urrp@123

set timeout -1

spawn sftp $user@$host
expect "*assword:*"
send "$password\r"


expect "sftp>"
send "cd /data/appdata/eaas/tlq/file\r"

expect "sftp>"
send "rename ${v_RemoteFileName} ${v_changeSFileName}\r"

expect "sftp>"
send "bye\r"

expect eof

