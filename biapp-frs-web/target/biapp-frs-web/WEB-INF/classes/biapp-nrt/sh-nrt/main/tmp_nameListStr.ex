#!/usr/bin/expect -f

#set port 22
#set user urrp
set user [lindex $argv 0]
set password [lindex $argv 1]
set host [lindex $argv 2]
set putYtjFilePath [lindex $argv 3]


#set host 10.1.244.73
#set host 10.200.250.25
#set password urrp@123

set timeout -1

spawn sftp $user@$host
expect "*assword:*"
send "$password\r"


expect "sftp>"
send "cd ${putYtjFilePath}\r"

expect "sftp>"
send "ls -l\r"

expect "sftp>"
send "bye\r"

expect eof

