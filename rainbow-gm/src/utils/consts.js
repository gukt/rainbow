const userTypes = ['普通', '高级']
const defaultLoadingText = '拼命加载中'
const datePickerShortcuts = {
  today: {
    text: '今天',
    onClick(picker) {
      picker.$emit('pick', new Date());
    }
  },
  yesterday: {
    text: '昨天',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() - 3600 * 1000 * 24);
      picker.$emit('pick', date)
    }
  },
  past2: {
    text: '2 天前',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 3);
      picker.$emit('pick', date)
    }
  },
  past3: {
    text: '3 天前',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 3);
      picker.$emit('pick', date)
    }
  },
  past7: {
    text: '一周前',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
      picker.$emit('pick', date)
    }
  },
  past30: {
    text: '一个月前',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 30);
      picker.$emit('pick', date)
    }
  },
  past60: {
    text: '二个月前',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 60);
      picker.$emit('pick', date)
    }
  },
  past90: {
    text: '三个月前',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 90);
      picker.$emit('pick', date)
    }
  },
  past180: {
    text: '半年前',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 180);
      picker.$emit('pick', date)
    }
  },
  past365: {
    text: '一年前',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 365);
      picker.$emit('pick', date)
    }
  },
  tomorrow: {
    text: '明天',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() + 3600 * 1000 * 24);
      picker.$emit('pick', date)
    }
  },
  next2: {
    text: '2 天后',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 2);
      picker.$emit('pick', date)
    }
  },
  next3: {
    text: '3 天后',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 3);
      picker.$emit('pick', date)
    }
  },
  next7: {
    text: '一周后',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 7);
      picker.$emit('pick', date)
    }
  },
  next30: {
    text: '一个月后',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime()    + 3600 * 1000 * 24 * 30);
      picker.$emit('pick', date)
    }
  },
  next90: {
    text: '三个月后',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 90);
      picker.$emit('pick', date)
    }
  },
  forever: {
    text: '地球末日',
    onClick(picker) {
      const date = new Date();
      date.setTime(date.getTime() + Number.MAX_SAFE_INTEGER);
      picker.$emit('pick', date)
    }
  }
}

export {
  userTypes,
  defaultLoadingText,
  datePickerShortcuts
}
