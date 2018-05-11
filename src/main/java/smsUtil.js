//Plugin

        function isGSMAlphabet(text) {
            var regexp = new RegExp("^[A-Za-z0-9 \\r\\n@£$¥èéùìòÇØøÅå\u0394_\u03A6\u0393\u039B\u03A9\u03A0\u03A8\u03A3\u0398\u039EÆæßÉ!\"#$%&'()*+,\\-./:;<=>?¡ÄÖÑÜ§¿äöñüà^{}\\\\\\[~\\]|\u20AC]*$");

            return regexp.test(text);
        }
//Start
$(function () {
    $.fn.smsArea = function (options) {
        //alert(options);
        var
                e = this,
                cutStrLength = 0,
                s = $.extend({
                    maxSmsNum: 4,
                    cut: false,
                    interval: 400,
                    preventSpace: false,
                    allChars: $('.counter'),
                    blocks: [{
                            counters: {
                                messagetxt: $('.smsCountTxt'),
                                message: $('.smsCount'),
                                character: $('.smsLength'),
                                unicode: $('.unicodeAlarm')
                            },

                            lengths: {
                                ascii: [160, 306, 459],
                                unicode: [70, 134, 201]
                            }
                        }]
                }, options);


        e.on('keyup', function () {
            //alert('mm');
            clearTimeout(this.timeout);
            this.timeout = setTimeout(function () {

                var
                        smsType,
                        smsLength = 0,
                        smsCount = -1,
                        charsLeft = 0,
                        text = e.val(),
                        isUnicode = false,
                        isGSM = true;
                isGSM = isGSMAlphabet(text);
                for (var charPos = 0; charPos < text.length; charPos++) {
                    switch (text[charPos]) {
                        case "[":
                        case "]":
                        case "\\":
                        case "^":
                        case "{":
                        case "}":
                        case "|":
                        case "€":
                        case "~":
                            smsLength += 2;
                            break;

                        default:
                            smsLength += 1;
                    }

                    //!isUnicode && text.charCodeAt(charPos) > 127 && text[charPos] != "€" && (isUnicode = true)
                    if (text.charCodeAt(charPos) > 127 && text[charPos] != "€")
                        isUnicode = true;
                }

                //alert(s.blocks.length);
                for (var oCount = 0; oCount < s.blocks.length; oCount++) {
                    //alert(oCount);
                    if (isUnicode)
                        smsType = s.blocks[oCount].lengths.unicode;
                    else
                        smsType = s.blocks[oCount].lengths.ascii;
                    for (var sCount = 0; sCount < s.maxSmsNum; sCount++) {
                        if (sCount >= smsType.length) {
                            break;
                        }
                        try {
                            cutStrLength = smsType[sCount];
                            if (smsLength <= smsType[sCount]) {

                                smsCount = sCount + 1;
                                charsLeft = smsType[sCount] - smsLength;
                                break
                            }
                        } catch (x) {
                            alert('errr')
                        }

                    }
                    if (text.length > cutStrLength) {
                        if (s.cut) {

                            text = text.substring(0, cutStrLength);
                            e.val(text);
                        } else {
                            //alert('zzz');
                            s.blocks[oCount].counters.message.html('no suport');
                            s.blocks[oCount].counters.character.html('N/A');
                            s.blocks[oCount].counters.unicode.html(!isGSM ? 'Not in GSM encode' : isUnicode ? 'Có ký tự unicode!' : '');
                            s.blocks[oCount].counters.messagetxt.val('no suport');

                        }
                    } else {
                        //this.value = this.value.replace(/\s/g, "");
                        //alert(e.val());
                        smsCount == -1 && (smsCount = s.maxSmsNum, charsLeft = 0);

                        s.blocks[oCount].counters.message.html(smsCount);
                        s.blocks[oCount].counters.character.html(charsLeft);
                        s.blocks[oCount].counters.unicode.html(!isGSM ? 'Charactor not GSM' : isUnicode ? 'Có ký tự unicode!' : '');
                        s.blocks[oCount].counters.messagetxt.val(smsCount);

                    }
                    try {
                        s.allChars.html(smsLength);
                        s.allCharsTxt.val(smsLength);
                    } catch (x) {
                    }
                }

                //s.counters.unicode.html(e.val());
                if (s.preventSpace) {
                    while (text.match(/\s\s/g)) {
                        text = text.replace(/\s\s/g, " ");
                    }
                    e.val(text);
                }

            }, s.interval);
            //alert(e.val());
        });
    };
    $('.smsTextAds').smsArea({
        maxSmsNum: 4,
        cut: false,
        preventSpace: true,
        allChars: $('.counter'),
        allCharsTxt: $('.counterTxt'),
        blocks: [
            {
                lengths: {
                    ascii: [160, 306, 459, 612],
                    unicode: [70, 134, 201, 612]
                },
                counters: {
                    messagetxt: $('.smsCountTxt'),
                    message: $('.smsCount'),
                    character: $('.smsLength'),
                    unicode: $('.unicodeAlarm')
                }
            },
            {
                lengths: {
                    ascii: [160, 306, 459, 612],
                    unicode: [70, 134, 201, 268]
                },
                counters: {
                    messagetxt: $('.smsViettelTxt'),
                    message: $('.smsViettelCount'),
                    character: $('.smsViettelLength'),
                    unicode: $('.smsViettelUnicodeAlarm')
                }
            },
            {
                lengths: {
                    ascii: [123, 269, 422, 575],
                    unicode: [70, 134, 201, 268]
                },
                counters: {
                    messagetxt: $('.smsVinaPhoneAdsTxt'),
                    message: $('.smsVinaPhoneAdsCount'),
                    character: $('.smsVinaPhoneAdsLength'),
                    unicode: $('.smsVinaPhoneAdsUnicodeAlarm')
                }
            },
            {
                lengths: {
                    ascii: [160, 306, 459, 612],
                    unicode: [70, 133, 200, 267]
                },
                counters: {
                    messagetxt: $('.smsMobiPhoneAdsTxt'),
                    message: $('.smsMobiPhoneAdsCount'),
                    character: $('.smsMobiPhoneAdsLength'),
                    unicode: $('.smsMobiPhoneUnicodeAdsAlarm')
                }
            },
            {
                lengths: {
                    ascii: [160, 306, 459, 612],
                    unicode: [70, 134, 201, 268]
                },
                counters: {
                    messagetxt: $('.smsVnmGtelTxt'),
                    message: $('.smsVnmGtelCount'),
                    character: $('.smsVnmGtelLength'),
                    unicode: $('.smsVnmGtelUnicodeAlarm')
                }
            }
        ]
    });

    $('.smsTextBulk').smsArea({
        maxSmsNum: 5,
        preventSpace: true,
        allChars: $('.counter'),
        allCharsTxt: $('.counterTxt'),
        blocks: [
            {
                lengths: {
                    ascii: [160, 306, 459, 612, 765],
                    unicode: [70, 134, 201, 268, 335]
                },
                counters: {
                    messagetxt: $('.smsCountTxt'),
                    message: $('.smsCount'),
                    character: $('.smsLength'),
                    unicode: $('.unicodeAlarm')
                }
            },
            {
                lengths: {
                    ascii: [160, 306, 459, 612, 765],
                    unicode: [70, 134, 201, 268, 335]
                },
                counters: {
                    messagetxt: $('.smsViettelTxt'),
                    message: $('.smsViettelCount'),
                    character: $('.smsViettelLength'),
                    unicode: $('.smsViettelUnicodeAlarm')
                }
            },
            {
                lengths: {
                    ascii: [160, 306, 459, 612],
                    unicode: [70, 134, 201, 268]
                },
                counters: {
                    messagetxt: $('.smsVinaPhoneAdsTxt'),
                    message: $('.smsVinaPhoneAdsCount'),
                    character: $('.smsVinaPhoneAdsLength'),
                    unicode: $('.smsVinaPhoneAdsUnicodeAlarm')
                }
            },
            {
                lengths: {
                    ascii: [160, 306, 459, 612],
                    unicode: [70, 133, 200, 267]
                },
                counters: {
                    messagetxt: $('.smsMobiPhoneAdsTxt'),
                    message: $('.smsMobiPhoneAdsCount'),
                    character: $('.smsMobiPhoneAdsLength'),
                    unicode: $('.smsMobiPhoneUnicodeAdsAlarm')
                }
            },
            {
                lengths: {
                    ascii: [160, 306, 459, 612],
                    unicode: [70, 134, 201, 268]
                },
                counters: {
                    messagetxt: $('.smsVnmGtelTxt'),
                    message: $('.smsVnmGtelCount'),
                    character: $('.smsVnmGtelLength'),
                    unicode: $('.smsVnmGtelUnicodeAlarm')
                }
            }
        ]
    });

    $('.smsViettel').smsArea({
        maxSmsNum: 4,
        preventSpace: false,
        lengths: {
            ascii: [160, 306, 459, 612],
            unicode: [70, 134, 201, 268]
        },
        counters: {
            message: $('.smsViettelCount'),
            character: $('.smsViettelLength'),
            unicode: $('.smsViettelUnicodeAlarm')
        }
    });

    $('.smsVinaPhoneBulk').smsArea({
        maxSmsNum: 4,
        preventSpace: false,
        lengths: {
            ascii: [160, 306, 459, 612],
            unicode: [70, 134, 201, 268]
        },
        counters: {
            message: $('.smsVinaPhoneBulkCount'),
            character: $('.smsVinaPhoneBulkLength'),
            unicode: $('.smsVinaPhoneBulkUnicodeAlarm')
        }
    });

    $('.smsVinaPhoneAds').smsArea({
        maxSmsNum: 4,
        cut: false,
        preventSpace: false,
        lengths: {
            ascii: [123, 269, 422, 575],
            unicode: [70, 134, 201, 268]
        },
        counters: {
            message: $('.smsVinaPhoneAdsCount'),
            character: $('.smsVinaPhoneAdsLength'),
            unicode: $('.smsVinaPhoneAdsUnicodeAlarm')
        }
    });
    $('.smsMobiPhoneBulk').smsArea({
        maxSmsNum: 4,
        cut: false,
        preventSpace: false,
        lengths: {
            ascii: [160, 306, 459, 612],
            unicode: [70, 134, 201, 268]
        },
        counters: {
            message: $('.smsMobiPhoneBulkCount'),
            character: $('.smsMobiPhoneBulkLength'),
            unicode: $('.smsMobiPhoneBulkUnicodeAlarm')
        }
    });
    $('.smsMobiPhoneAds').smsArea({
        maxSmsNum: 4,
        cut: false,
        preventSpace: false,
        lengths: {
            ascii: [127, 273, 426, 579],
            unicode: [70, 134, 201, 268]
        },
        counters: {
            message: $('.smsMobiPhoneAdsCount'),
            character: $('.smsMobiPhoneAdsLength'),
            unicode: $('.smsMobiPhoneUnicodeAdsAlarm')
        }
    });
    $('.smsVnmGtel').smsArea({
        maxSmsNum: 4,
        cut: false,
        preventSpace: false,
        lengths: {
            ascii: [160, 306, 459, 612],
            unicode: [70, 134, 201, 268]
        },
        counters: {
            message: $('.smsVnmGtelCount'),
            character: $('.smsVnmGtelLength'),
            unicode: $('.smsVnmGtelUnicodeAlarm')
        }
    });
    $('.escapeSpace').on('keyup', function () {
        var text = $(this).val();
        while (text.match(/\s\s/g)) {
            text = text.replace(/\s\s/g, " ");
        }
        $(this).val(text);
    });
})