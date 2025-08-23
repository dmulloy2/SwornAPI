package net.dmulloy2.swornapi.io;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.dizitart.no2.repository.annotations.Id;

@Data
public abstract class AbstractPlayerData implements ConfigurationSerializable
{
	@Id
	protected UUID id;

	public AbstractPlayerData() { }

	public AbstractPlayerData(Map<String, Object> data)
	{
		FileSerialization.deserialize(this, data);
	}

	@Override
	public Map<String, Object> serialize()
	{
		return FileSerialization.serialize(this);
	}
}
