defmodule Lily.Commands.Server do
  use Agent

  def start_link(_init \\ :ok),
    do: Agent.start_link(fn -> %{} end, name: __MODULE__)

  def fetch_command(name), do:
    Agent.get(__MODULE__, &Map.get(&1, name))

  def add_command(name, function), do:
    Agent.update(__MODULE__, &Map.put(&1, name, function))
end