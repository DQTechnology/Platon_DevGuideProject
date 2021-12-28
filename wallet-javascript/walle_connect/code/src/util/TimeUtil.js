export default class TimeUtil {


    static GetCurTimestampWithMilliSec() {

        return new Date().getTime();
    }

    static bufferToNumber(format, str, matchChar, formatLen, ctx) {
        ctx.timeNO = 0;
        for (; ctx.curIndex < formatLen; ++ctx.curIndex) {
            let ch = format[ctx.curIndex];
            let cCh = str[ctx.curIndex];
            if (ch != matchChar || (cCh < '0' || cCh > '9')) {
                --ctx.curIndex;
                break;
            }
            ctx.timeNO *= 10;
            ctx.timeNO += (cCh - '0');
        }
    }

    static StringToDateTime(str, format = "yyyy-MM-dd hh:mm:ss") {

        let formatLen = format.length;
        let strLen = str.length;
        if (strLen < formatLen) {
            return;
        }
        let parseObj = { y: 0, M: 0, d: 0, h: 0, m: 0, s: 0, S: 0 };
        let ctx = {
            timeNO: 0,
            curIndex: 0,
        };
        for (let i = 0; i < formatLen; ++i) {
            let ch = format[i];
            if (ch === 'y' || ch === 'M' || ch === 'd' || ch === 'h' || ch === 'm' || ch === 's' || ch === 'S') {
                ctx.curIndex = i;
                TimeUtil.bufferToNumber(format, str, ch, formatLen, ctx);
                parseObj[ch] = ctx.timeNO;
                i = ctx.curIndex;
            } else {
                if (ch != str[i]) {
                    return;
                }
            }
        }
        return new Date(parseObj.y, parseObj.M - 1, parseObj.d, parseObj.h, parseObj.m, parseObj.s, parseObj.S);
    }

    static fillBuffer(format, matchChar, formatLen, timeNO, buffer, ctx) {
        let symbolNum = 0;
        for (; ctx.curIndex < formatLen; ++ctx.curIndex) {
            let ch = format[ctx.curIndex];
            if (ch == matchChar) {
                ++symbolNum;
            } else {
                --ctx.curIndex;
                break;
            }
        }
        if (ctx.curIndex == formatLen) {
            --ctx.curIndex;
        }
        let begin = 1;
        for (let j = 0; j < symbolNum; ++j) {
            buffer[ctx.curIndex - j] = Math.floor((timeNO / (begin)) % 10);
            begin *= 10;
        }
    }

    static TimestampToString(timestamp, format = "yyyy-MM-dd hh:mm:ss") {
        let date = new Date(timestamp);
        return TimeUtil.DateTimeToString(date, format);
    }

    static DateTimeToString(date, format = "yyyy-MM-dd hh:mm:ss") {
        let formatLen = format.length;
        let chArr = [];
        let parseObj = {
            y: date.getFullYear(), M: date.getMonth() + 1,
            d: date.getDate(), h: date.getHours(), m: date.getMinutes(),
            s: date.getSeconds(), S: date.getMilliseconds()
        };
        let ctx = {
            curIndex: 0,
        };
        for (let i = 0; i < formatLen; ++i) {
            let ch = format[i];
            if (ch === 'y' || ch === 'M' || ch === 'd' || ch === 'h' || ch === 'm' || ch === 's' || ch === 'S') {
                ctx.curIndex = i;
                TimeUtil.fillBuffer(format, ch, formatLen, parseObj[ch], chArr, ctx);
                i = ctx.curIndex;
            } else {
                chArr[i] = ch;
            }
        }
        return chArr.join('');
    }

    static GetCurrDate() {
        return new Date();
    }
    //获取当前月份第一天时间
    static GetMonthFirstDate() {
        let curDate = new Date();
        return TimeUtil.GetPtMonthFirstDate(curDate);
    }

    // 获取指定月份的第一天
    static GetPtMonthFirstDate(ptDate) {
        let day = ptDate.getDate();
        return TimeUtil.GetPreDate(ptDate, day - 1);
    }
    // 获取当前月份最后一天时间
    static GetMonthLastDate() {
        let curDate = new Date();
        return TimeUtil.GetPtMonthLastDate(curDate);
    }
    // 获取指定月份最后一天
    static GetPtMonthLastDate(ptDate) {

        return new Date(ptDate.getFullYear(), ptDate.getMonth() + 1, 0);

    }
    // 获取前一天时间
    static GetPreDate(ptDate, days) {
        return new Date(ptDate.getTime() - 24 * 60 * 60 * 1000 * days);
    }
    //获取后一天时间
    static GetNextDate(ptDate, days) {
        return new Date(ptDate.getTime() + 24 * 60 * 60 * 1000 * days);
    }


}