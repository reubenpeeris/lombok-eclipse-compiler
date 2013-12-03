package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

public class ListLogger extends AbstractLogger {
	public ListLogger() {
		super(Logger.LEVEL_DEBUG, "ListLogger");
	}
	
	private List<Message> messages = new ArrayList<Message>();
	
	private void log(int level, String message, Throwable throwable) {
		messages.add(new Message(level, message, throwable));
	}
	
	public List<Message> getMessages() {
		List<Message> messages = this.messages;
		this.messages = new ArrayList<Message>();
		
		return messages;
	}
	
	@Override
	public void debug(String message, Throwable throwable) {
		log(Logger.LEVEL_DEBUG, message, throwable);
	}

	@Override
	public void info(String message, Throwable throwable) {
		log(Logger.LEVEL_INFO, message, throwable);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		log(Logger.LEVEL_WARN, message, throwable);
	}

	@Override
	public void error(String message, Throwable throwable) {
		log(Logger.LEVEL_ERROR, message, throwable);
	}

	@Override
	public void fatalError(String message, Throwable throwable) {
		log(Logger.LEVEL_FATAL, message, throwable);
	}

	@Override
	public Logger getChildLogger(String name) {
		throw new UnsupportedOperationException();
	}
	
	public static class Message {
		private final int level;
		private final String message;
		private final Throwable throwable;
		
		public Message(int level, String message, Throwable throwable) {
			this.level = level;
			this.message = message;
			this.throwable = throwable;
		}
		
		public Message(int level, String message) {
			this(level, message, null);
		}

		public int getLevel() {
			return level;
		}

		public String getMessage() {
			return message;
		}

		public Throwable getThrowable() {
			return throwable;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + level;
			result = prime * result + ((message == null) ? 0 : message.hashCode());
			result = prime * result + ((throwable == null) ? 0 : throwable.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Message other = (Message) obj;
			if (level != other.level)
				return false;
			if (message == null) {
				if (other.message != null)
					return false;
			} else if (!message.equals(other.message))
				return false;
			if (throwable == null) {
				if (other.throwable != null)
					return false;
			} else if (!throwable.equals(other.throwable))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Message [level=" + level + ", message=" + message + ", throwable=" + throwable + "]";
		}
	}
}