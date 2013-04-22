/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/agpl.html>
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.core.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class RawDrop {
	
	private String channel;
	
	@JsonProperty("droplet_title")
	private String title;
	
	@JsonProperty("droplet_raw")
	private String raw;
	
	@JsonProperty("droplet_content")
	private String content;
	
	@JsonProperty("identity_orig_id")
	private String identityOriginalId;
	
	@JsonProperty("identity_username")
	private String identityUsername;
	
	@JsonProperty("identity_avatar")
	private String identityAvatar;
	
	@JsonProperty("identity_name")
	private String identityName;
	
	@JsonProperty("droplet_orig_id")
	private String dropOriginalId;
	
	@JsonProperty("droplet_locale")
	private String locale;
	
	@JsonProperty("droplet_date_pub")
	private String datePublished;
	
	@JsonProperty("droplet_type")
	private String dropType;
	
	private String source;
	
	@JsonProperty("semantics_complete")
	private boolean semanticsComplete;
	
	@JsonProperty("media_complete")
	private boolean mediaComplete;
	
	@JsonProperty("channel_ids")
	private List<Long> channelIds;

	@JsonProperty("river_id")
	private List<Long> riverIds;
	
	private List<Link> links;
	
	private List<Media> media;
	
	private List<Tag> tags;
	
	private List<Place> places;
	
	private Long deliveryTag;
	
	@JsonProperty("rules_complete")
	private boolean rulesComplete;
	
	@JsonProperty("bucket_id")
	private List<Long> bucketIds;
	
	/** List of river ids for which this drop is to be marked as read */
	@JsonProperty("mark_as_read")
	private List<Long> markAsRead;

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIdentityOriginalId() {
		return identityOriginalId;
	}

	public void setIdentityOriginalId(String identityOriginalId) {
		this.identityOriginalId = identityOriginalId;
	}

	public String getIdentityUsername() {
		return identityUsername;
	}

	public void setIdentityUsername(String identityUsername) {
		this.identityUsername = identityUsername;
	}

	public String getIdentityAvatar() {
		return identityAvatar;
	}

	public void setIdentityAvatar(String identityAvatar) {
		this.identityAvatar = identityAvatar;
	}

	public String getIdentityName() {
		return identityName;
	}

	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}

	public String getDropOriginalId() {
		return dropOriginalId;
	}

	public void setDropOriginalId(String dropOriginalId) {
		this.dropOriginalId = dropOriginalId;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(String datePublished) {
		this.datePublished = datePublished;
	}

	public String getDropType() {
		return dropType;
	}

	public void setDropType(String dropType) {
		this.dropType = dropType;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isSemanticsComplete() {
		return semanticsComplete;
	}

	public void setSemanticsComplete(boolean semanticsComplete) {
		this.semanticsComplete = semanticsComplete;
	}

	public boolean isMediaComplete() {
		return mediaComplete;
	}

	public void setMediaComplete(boolean mediaComplete) {
		this.mediaComplete = mediaComplete;
	}

	public List<Long> getChannelIds() {
		return channelIds;
	}

	public void setChannelIds(List<Long> channelIds) {
		this.channelIds = channelIds;
	}

	public List<Long> getRiverIds() {
		return riverIds;
	}

	public void setRiverIds(List<Long> riverIds) {
		this.riverIds = riverIds;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Media> getMedia() {
		return media;
	}

	public void setMedia(List<Media> media) {
		this.media = media;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	public Long getDeliveryTag() {
		return deliveryTag;
	}

	public void setDeliveryTag(Long deliveryTag) {
		this.deliveryTag = deliveryTag;
	}

	public boolean isRulesComplete() {
		return rulesComplete;
	}

	public void setRulesComplete(boolean rulesComplete) {
		this.rulesComplete = rulesComplete;
	}

	public List<Long> getBucketIds() {
		return bucketIds;
	}

	public void setBucketIds(List<Long> bucketIds) {
		this.bucketIds = bucketIds;
	}

	public List<Long> getMarkAsRead() {
		return markAsRead;
	}

	public void setMarkAsRead(List<Long> markAsRead) {
		this.markAsRead = markAsRead;
	}

	public static class Link {
		
		private String url;
		
		@JsonProperty("original_url")
		private Boolean originalUrl;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Boolean getOriginalUrl() {
			return originalUrl;
		}

		public void setOriginalUrl(Boolean originalUrl) {
			this.originalUrl = originalUrl;
		}
	}
	
	public static class Media {
		
		private String url;
		
		private String type;
		
		@JsonProperty("drop_image")
		private String dropImage;
		
		private List<Thumbnail> thumbnails;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getDropImage() {
			return dropImage;
		}

		public void setDropImage(String dropImage) {
			this.dropImage = dropImage;
		}

		public List<Thumbnail> getThumbnails() {
			return thumbnails;
		}

		public void setThumbnails(List<Thumbnail> thumbnails) {
			this.thumbnails = thumbnails;
		}
	}
	
	public static class Thumbnail {
		
		private Integer size;
		
		private String url;

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
		
	}
	
	public static class Tag {
		
		@JsonProperty("tag_name")
		private String name;
		
		@JsonProperty("tag_type")
		private String type;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
	}
	
	public static class Place {
		
		@JsonProperty("place_name")
		private String name;
		
		private Float latitude;
		
		private Float longitude;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Float getLatitude() {
			return latitude;
		}

		public void setLatitude(Float latitude) {
			this.latitude = latitude;
		}

		public Float getLongitude() {
			return longitude;
		}

		public void setLongitude(Float longitude) {
			this.longitude = longitude;
		}
		
	}

}
