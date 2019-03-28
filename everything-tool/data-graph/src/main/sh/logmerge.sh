dir=$(dirname $0)

if [ "$#" == "0" ] || ([ "$#" == "1" ] && [ "$1" == "--help" ])
then
	echo "logmerge [option...] file1 file2..."
	echo "option:"
	echo "  --trim-head: remove blank characters in the head of logs"
	echo "  --trim-tail: remove blank characters in the tail of logs"
	echo "  --delimiter=xx: split the log file with delimiter"
	echo "  --output-delimiter=xx: seperate the output logs with specified delimiter"
	echo "  --region=a,b: set the region of logs for comparasion, default value is the whole log"
	echo "  --charset=utf8: set the charset used to read file and write output"
	exit 0
fi

java -jar ${dir}/../lib/log-merge-1.0-SNAPSHOT.jar $@